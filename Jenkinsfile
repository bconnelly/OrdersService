pipeline{
    agent{
        docker{
            image 'bryan949/poc-agent:0.2.1'
            args '-v /root/.m2:/root/.m2 \
                  -v /root/jenkins/restaurant-resources/:/root/jenkins/restaurant-resources/ \
                  -v /var/run/docker.sock:/var/run/docker.sock \
                  --privileged --env KOPS_STATE_STORE=${KOPS_STATE_STORE} \
                  --env DOCKER_USER=${DOCKER_USER} --env DOCKER_PASS=${DOCKER_PASS}'
            alwaysPull true
        }
    }
    environment{
        AWS_SECRET_ACCESS_KEY = credentials('AWS_SECRET_ACCESS_KEY')
        AWS_ACCESS_KEY_ID = credentials('AWS_ACCESS_KEY_ID')
    }
    stages{
        stage('maven build and test'){
            steps{
                script{
                    env.GIT_SHA = sh(script: '''
                                                git clone ${ORDERS_REPO}
                                                cd OrdersService
                                                git rev-parse master
                                             ''', returnStdout: true).trim()
                    env.MASTER_COMMIT = sh(script: 'git rev-parse --short HEAD')
                    env.PREV_IMAGE = sh(script: '''
                                                   docker pull bryan949/poc-customers:latest
                                                   docker inspect --format='{{index .RepoDigests 0}}' bryan949/poc-customers:latest
                                                ''', returnStdout: true).trim()
                }
                sh '''
                    mvn verify
                   '''
                stash name: 'orders-repo', useDefaultExcludes: false

            }
        }
        stage('build and push docker image'){
            steps{
                unstash 'orders-repo'
                sh '''
                    cp /root/jenkins/restaurant-resources/tomcat-users.xml .
                    cp /root/jenkins/restaurant-resources/context.xml .
                    cp /root/jenkins/restaurant-resources/server.xml .

                    docker build -t bryan949/poc-orders:${GIT_SHA} .
                    docker tag bryan949/poc-orders:${GIT_SHA} bryan949/poc-orders:latest
                    docker push bryan949/poc-orders:${GIT_SHA}
                    docker push bryan949/poc-orders:latest

                    rm tomcat-users.xml
                    rm context.xml
                    rm server.xml
                '''
            }
        }
        stage('configure cluster connection'){
            steps{
    	        sh '''
	                kops export kubecfg --admin --name poc.k8s.local
	                if [ -z "$(kops validate cluster | grep ".k8s.local is ready")" ]; then exit 1; fi
	                kubectl config set-context --current --namespace rc
	            '''
            }
        }
        stage('deploy services to cluster - rc namespace'){
            steps{
                sh '''
                    git clone https://github.com/bconnelly/Restaurant-k8s-components.git

                    find Restaurant-k8s-components/orders -type f -path ./Restaurant-k8s-components/orders -prune -o -name *.yaml -print | while read line; do yq -i '.metadata.namespace = "rc"' $line > /dev/null; done
                    yq -i '.metadata.namespace = "rc"' /root/jenkins/restaurant-resources/poc-secrets.yaml > /dev/null
                    yq -i '.metadata.namespace = "rc"' Restaurant-k8s-components/poc-config.yaml > /dev/null
                    yq -i '.metadata.namespace = "rc"' Restaurant-k8s-components/mysql-external-service.yaml > /dev/null

                    kubectl apply -f /root/jenkins/restaurant-resources/poc-secrets.yaml
                    kubectl apply -f Restaurant-k8s-components/orders/
                    kubectl apply -f Restaurant-k8s-components/poc-config.yaml
                    kubectl apply -f Restaurant-k8s-components/mysql-external-service.yaml
                    kubectl get deployment
                    kubectl rollout restart deployment orders-deployment

                    if [ -z "$(kops validate cluster | grep ".k8s.local is ready")" ]; then echo "failed to deploy to rc namespace" && exit 1; fi
                    sleep 3
                '''
                stash includes: 'Restaurant-k8s-components/orders/', name: 'k8s-components'
                stash includes: 'Restaurant-k8s-components/tests.py,Restaurant-k8s-components/tests.py', name: 'tests'
            }
        }
        stage('sanity tests'){
            steps{
                unstash 'tests'
                sh '''
                    python Restaurant-k8s-components/tests.py ${RC_LB}
                    exit_status=$?
                    if [ "${exit_status}" -ne 0 ];
                    then
                        echo "Tests failed. Exit status: ${exit_status}"
                        exit ${exit_status}
                    fi
                '''
                withCredentials([gitUsernamePassword(credentialsId: 'GITHUB_USERPASS', gitToolName: 'Default')]) {
                    sh '''
                        git checkout rc
                        git checkout master
                        git merge rc
                        git push origin master
                    '''
                }
            }
        }
        stage('deploy to cluster - prod namespace'){
            steps{
                unstash 'k8s-components'

                sh '''
                    find Restaurant-k8s-components/orders -type f -path ./Restaurant-k8s-components/orders -prune -o -name *.yaml -print | while read line; do yq -i '.metadata.namespace = "prod"' $line > /dev/null; done
                    yq -i '.metadata.namespace = "prod"' /root/jenkins/restaurant-resources/poc-secrets.yaml > /dev/null
                    yq -i '.metadata.namespace = "prod"' Restaurant-k8s-components/poc-config.yaml > /dev/null
                    yq -i '.metadata.namespace = "prod"' Restaurant-k8s-components/mysql-external-service.yaml > /dev/null

                    kubectl config set-context --current --namespace prod
                    kubectl apply -f /root/jenkins/restaurant-resources/poc-secrets.yaml
                    kubectl apply -f Restaurant-k8s-components/orders/
                    kubectl apply -f Restaurant-k8s-components/poc-config.yaml
                    kubectl apply -f Restaurant-k8s-components/mysql-external-service.yaml
                    kubectl get deployment
                    kubectl rollout restart deployment orders-deployment

                    if [ -z "$(kops validate cluster | grep ".k8s.local is ready")" ]; then echo "PROD FAILURE"; fi
                    sleep 3
                '''
            }
        }
    }
    post{
        failure{
            unstash 'orders-repo'
            withCredentials([gitUsernamePassword(credentialsId: 'GITHUB_USERPASS', gitToolName: 'Default')]) {
                sh '''
                    echo "Reverting git master branch to previous commit ${MASTER_COMMIT}"
                    git checkout master
                    git reset --hard ${MASTER_COMMIT}
                    git push origin master --force
                '''
            }
        }
        always{
            cleanWs(cleanWhenAborted: true,
                    cleanWhenFailure: true,
                    cleanWhenNotBuilt: true,
                    cleanWhenSuccess: true,
                    cleanWhenUnstable: true,
                    cleanupMatrixParent: true,
                    deleteDirs: true,
                    disableDeferredWipeout: true)

            sh '''
                docker rmi bryan949/poc-orders:${GIT_SHA} || true
                docker rmi bryan949/poc-orders:latest || true
                docker image prune || true
            '''
        }
    }
}