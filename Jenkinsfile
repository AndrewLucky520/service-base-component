def docker_image = "${env.DOCKER_REGISTRY_URL}"
pipeline {
    agent { label 'Slave1' }
    options {
        //超时一小时
        timeout(time: 1, unit: 'HOURS') 
    }
// #######################需要修改的区域 开始#################################
    environment {
        // 注册中心域名默认是
        EUREKA_NAME='register'
        // 修改为服务的域名
        DOMAIN_NAME='service-base-component'
        // docker容器内部端口，不建议修改
        CONTAINER_PORT=80              
        // 修改为服务的端口
        APP_PORT=48113
        // 注册中心认证的用户密码,没有就设置为空
        EUREKA_USR_PASSWD="zhx:zhx123@"
    }
    // 注意需要配置选择 profile,测试环境的值为test,生产环境的值为prod
    parameters {
        // 修改为子项目名称
        string(name:'module', defaultValue: "service-base-component", description: '发布模块')
        // 修改为你部署栈的名称
        string(name:'stack_name', defaultValue: "zhx", description: '发布的栈名称')
// ########################需要修改的区域 结束################################
        //发布集群地址  IP:PORT:
        string(name:'swarm', defaultValue: "${env.DOCKER_SWARM_ADDR}", description: '发布集群地址')
    }
    tools {
        maven 'maven3' 
        jdk 'jdk8'
    }
    stages {
        stage('参数') {
            steps {              
                echo "发布集群地址：${params.swarm}"  
                echo "发布模块：${params.module}"
                sh 'java -version'
                sh 'mvn --version'
                script{
                    if (params.Tag) {
                        tag = "$params.Tag"
                    } else {
                        sh "git rev-parse --short HEAD > commit-id"
                        tag = readFile('commit-id').replace("\n", "").replace("\r", "")
                    }
                    docker_image = "$docker_image/${params.stack_name}/${params.module}:$tag"
                    // 若为svn版本库时注释本块其他代码，打开下面注释
                    // docker_image = "$docker_image/${params.stack_name}/${params.module}:${env.BUILD_NUMBER}"
                }

            }        
        }
        stage('编译JAR包') {
            steps {
                dir("${env.WORKSPACE}") {
                  echo "开始使用maven编译并打包成docker镜像......"
                  sh "mvn clean package -Dmaven.test.skip=true"
                }
            }      
        }
        stage('编译DOCKER镜像') {
            steps { 
                dir("${env.WORKSPACE}") {
                  echo "整理JAR包，准备打包成DOCKER镜像......"  
                  sh "docker build -t $docker_image --build-arg JAR_FILE=${params.module}.jar ."
                  echo "整理JAR包，准备打包成DOCKER镜像......"  
                }
            }      
        }
        stage('上传镜像') {         
            steps {
                dir("${env.WORKSPACE}") {
                 echo "上传本地Docker镜像到私服......"  
                  sh "docker push $docker_image"
                }
            }     
        }        
        stage('编排部署') {
            steps {
                dir("${env.WORKSPACE}") {
                   echo '部署到生产环境'
                   sh "pwd"
                   script {
                       if (params.profile != 'test' && params.profile != 'dev' && params.profile != 'devk') {
                           EUREKA_SERVER_ADDRESS="http://${EUREKA_USR_PASSWD}${EUREKA_NAME}/eureka,http://${EUREKA_USR_PASSWD}${EUREKA_NAME}2/eureka,http://${EUREKA_USR_PASSWD}${EUREKA_NAME}3/eureka"
                       } else  {
                           EUREKA_SERVER_ADDRESS="http://${EUREKA_USR_PASSWD}${EUREKA_NAME}/eureka"
                       }
                   }
                    echo '设置服务名称'
                   sh "sed -i 's/__DOMAIN_NAME__/${env.DOMAIN_NAME}/g' docker-compose.yml"
                   echo '设置环境变量'
	               sh "export docker_image=${docker_image} &&  export PROFILE=${params.profile} &&  export EUREKA_SERVER_ADDRESS=$EUREKA_SERVER_ADDRESS && docker -H ${params.swarm} stack deploy -c docker-compose.yml --with-registry-auth ${params.stack_name}"
                }
            }        
        }        
    }

    post {        
        always {            
            echo '执行完成。'            
        }        
        success {            
            echo '恭喜你，发布成功了!'        
        }        
        unstable {            
            echo 'I am unstable :/'        
        }        
        failure {            
            echo '发布失败啦，请查明原因哦！'        
        }        
        changed {            
            echo 'Things were different before...'        
        }    
    }
}
