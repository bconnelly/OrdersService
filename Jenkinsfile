pipeline{
    agent{
        docker{
            image 'bryan949/fullstack-agent:0.1'
            args '-v /root/.m2:/root/.m2 \
                  -v /root/jenkins/restaurant-resources/:/root/jenkins/restaurant-resources/ \
                  -v /var/run/docker.sock:/var/run/docker.sock \
                  --privileged --env KOPS_STATE_STORE=' + env.KOPS_STATE_STORE + \
                  ' --env DOCKER_USER=' + env.DOCKER_USER + ' --env DOCKER_PASS=' + env.DOCKER_PASS
            alwaysPull true
        }
    }
    stages{
        stage('maven build and test, docker build and push'){
            steps{
                echo 'packaging and testing:'
                sh '''
                    mvn verify
                '''
                stash name: 'orders-repo'

            }
        }
        stage('build docker images'){
            steps{
                unstash 'orders-repo'
                sh '''
                    cat /root/jenkins/restaurant-resources/dockerhub-pass | docker login --username=$DOCKER_USER --password-stdin
                    cp /root/jenkins/restaurant-resources/tomcat-users.xml .
                    cp /root/jenkins/restaurant-resources/context.xml .
                    cp /root/jenkins/restaurant-resources/server.xml .

                    docker build -t bryan949/fullstack-orders .
                    docker push bryan949/fullstack-orders:latest

                    rm tomcat-users.xml
                    rm context.xml
                    rm server.xml
                '''
            }
        }
        stage('configure cluster connection'){
            steps{
    	        sh '''
	                kops export kubecfg --admin --name fullstack.k8s.local
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
                    yq -i '.metadata.namespace = "rc"' /root/jenkins/restaurant-resources/fullstack-secrets.yaml > /dev/null
                    yq -i '.metadata.namespace = "rc"' Restaurant-k8s-components/fullstack-config.yaml > /dev/null
                    yq -i '.metadata.namespace = "rc"' Restaurant-k8s-components/mysql-external-service.yaml > /dev/null

                    kubectl apply -f /root/jenkins/restaurant-resources/fullstack-secrets.yaml
                    kubectl apply -f Restaurant-k8s-components/orders/
                    kubectl get deployment
                    kubectl rollout restart deployment orders-deployment

                    if [ -z "$(kops validate cluster | grep ".k8s.local is ready")" ]; then echo "failed to deploy to rc namespace" && exit 1; fi
                '''
                stash includes: 'Restaurant-k8s-components/', name: 'k8s-components'
            }
        }
        stage('integration testing'){
            steps{
                unstash 'orders-repo'
                sh '''
                    export LOAD_BALANCER="a04b190e2ed854520b53460848db7d4c-fe8666faafe374b5.elb.us-east-1.amazonaws.com"
                    export SERVICE_PATH="RestaurantService"
                    export CUSTOMER_NAME=$RANDOM

                    SEAT_CUSTOMER_RESULT=$(curl -X POST -s -o /dev/null -w '%{http_code}' -d "firstName=$CUSTOMER_NAME&address=someaddress&cash=1.23" $LOAD_BALANCER/$SERVICE_PATH/seatCustomer)
                     if [ "$SEAT_CUSTOMER_RESULT" != 200 ]; then echo "$SEAT_CUSTOMER_RESULT" && exit 1; fi

                    GET_OPEN_TABLES_RESULT="$(curl -s -o /dev/null -w %{http_code} $LOAD_BALANCER/$SERVICE_PATH/getOpenTables)"
                    if [ "$GET_OPEN_TABLES_RESULT" != 200 ]; then echo "$GET_OPEN_TABLES_RESULT" && exit 1; fi

                    SUBMIT_ORDER_RESULT="$(curl -X POST -s -o /dev/null -w %{http_code} -d "firstName=$CUSTOMER_NAME&tableNumber=1&dish=food&bill=1.23" $LOAD_BALANCER/$SERVICE_PATH/submitOrder)"
                    if [ "$SUBMIT_ORDER_RESULT" != 200 ]; then echo "$SUBMIT_ORDER_RESULT" && exit 1; fi

                    BOOT_CUSTOMER_RESULT="$(curl -X POST -s -o /dev/null -w %{http_code} -d "firstName=$CUSTOMER_NAME" $LOAD_BALANCER/$SERVICE_PATH/bootCustomer)"
                    if [ "$BOOT_CUSTOMER_RESULT" != 200 ]; then echo "$GET_OPEN_TABLES_RESULT" && exit 1; fi
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
                    yq -i '.metadata.namespace = "prod"' /root/jenkins/restaurant-resources/fullstack-secrets.yaml > /dev/null
                    yq -i '.metadata.namespace = "prod"' Restaurant-k8s-components/fullstack-config.yaml > /dev/null
                    yq -i '.metadata.namespace = "prod"' Restaurant-k8s-components/mysql-external-service.yaml > /dev/null

                    kubectl config set-context --current --namespace prod
                    kubectl apply -f /root/jenkins/restaurant-resources/fullstack-secrets.yaml
                    kubectl apply -f Restaurant-k8s-components/orders/
                    kubectl get deployment
                    kubectl rollout restart deployment orders-deployment

                    if [ -z "$(kops validate cluster | grep ".k8s.local is ready")" ]; then echo "PROD FAILURE"; fi
                '''
            }
        }
    }
    post{
        failure{
            unstash 'orders-repo'
            withCredentials([gitUsernamePassword(credentialsId: 'GITHUB_USERPASS', gitToolName: 'Default')]) {
                sh '''
                    git checkout rc
                    git checkout master
                    git rev-list --left-right master...rc | while read line
                    do
                        COMMIT=$(echo $line | sed 's/[^0-9a-f]*//g')
                        git revert $COMMIT --no-edit
                    done
                    git merge rc
                    git push origin master
                '''
            }
        }
        always{
            sh '''
                docker rmi bryan949/fullstack-orders
                docker image prune
            '''

            cleanWs(cleanWhenAborted: true,
                    cleanWhenFailure: true,
                    cleanWhenNotBuilt: true,
                    cleanWhenSuccess: true,
                    cleanWhenUnstable: true,
                    cleanupMatrixParent: true,
                    deleteDirs: true,
                    disableDeferredWipeout: true)
        }
    }
}
//1