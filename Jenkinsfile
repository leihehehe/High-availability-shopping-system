
pipeline{
    agent any
    tools{
        maven 'maven-3.8.6'
    }
    environment{
        registryCredential = 'aws_credential'
        projectName = 'shopping'
        imageRegistry = '959241342292.dkr.ecr.ap-southeast-2.amazonaws.com'
        imageRegistryZone = 'ecr:ap-southeast-2'
    }
    stages{
        // stage('increment version'){
        //     steps{
        //         script{
        //             echo 'incrementing app version..'
        //             for(service_name in service_names.tokenize(',')){
        //                 sh "cd ${service_name} && mvn build-helper:parse-version versions:set \
        //                 -DnewVersion=\\\${parsedVersion.majorVersion}.\\\${parsedVersion.minorVersion}.\\\${parsedVersion.nextIncrementalVersion}-\${parsedVersion.qualifier} versions:commit"
        //             }

        //         }
        //     }
        // }
        stage('Maven build for services'){
            steps{
                script{
                    sh 'mvn clean install -Dmaven.test.skip=true'
                    sh 'mvn -f common clean install -Dmaven.test.skip=true'
                    sh 'mvn -f common/common-util clean install -Dmaven.test.skip=true'
                    sh 'mvn -f common/service-util clean install -Dmaven.test.skip=true'
                    sh 'mvn -f model clean install -Dmaven.test.skip=true'
                    sh 'mvn -f service clean install -Dmaven.test.skip=true'
                    for(service_name in service_names.tokenize(',')){
                        def service_real_name = service_name
                        if(service_name.contains('/')){
                            //for example, service/deal-service -> deal-service
                            service_real_name=service.tokenize('/')[1]
                        }
                        sh "mvn -f ${service_name} clean package -Dmaven.test.skip=true"
                    }
                }

            }

        }
        stage("Image build and upload"){
            steps{
                script{
                    for(service_name in service_names.tokenize(',')){
                        def service_real_name = service_name
                        if(service_name.contains('/')){
                            service_real_name=service.tokenize('/')[1]
                        }

                        def pom = readMavenPom file: "${service_name}/pom.xml"
                        // def matcher = readFile(service_name+'/pom.xml') =~ '<version>(.+)</version>'
                        // def IMAGE_VERSION = matcher[0][1]
                        // matcher=null
                        sh "~/docker_rm.sh ${service_real_name}"

                        sh "cd ${service_name} && docker build -t ${imageRegistry}/${projectName}-${service_real_name}:${pom.version} ."
                        docker.withRegistry("https://${imageRegistry}", "${imageRegistryZone}:${registryCredential}") {
                            docker.image("${imageRegistry}/${projectName}-${service_real_name}:${pom.version}").push()
                        }
                    }
                }
            }
        }
        stage("deploy to EC2"){
            steps{
                script{
                    for(service_name in service_names.tokenize(',')){
                        def service_real_name = service_name
                        if(service_name.contains('/')){
                            service_real_name=service.tokenize('/')[1]
                        }

                        def pom = readMavenPom file: "${service_name}/pom.xml"
                        def yaml = readYaml file: "${service_name}/src/main/resources/application.yml"
                        def port = yaml.server.port
                        def dockerCmd = "docker run -p ${port}:${port} -d --name ${service_real_name} ${imageRegistry}/${projectName}-${service_real_name}:${pom.version}"
                        def clearContainerImageCmd = "./docker_rm.sh ${service_real_name}"
                        def ecrLoginCmd="aws ecr get-login-password --region ${imageRegistryZone}| docker login --username AWS --password-stdin ${imageRegistry}"
                        sshagent(['ec2-server-key-sydney']) {
                            //sh "scp server-cmds.sh ec2-user@3.24.244.77:/home/ec2-user"
                            sh "ssh -o StrictHostKeyChecking=no ec2-user@3.24.244.77 \"${ecrLoginCmd}; ${clearContainerImageCmd}; ${dockerCmd}\""
                        }
                    }

                }
            }
        }

    }
}
