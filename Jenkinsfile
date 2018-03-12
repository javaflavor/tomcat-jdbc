#!groovy

node('maven') {
  //def mvnCmd = "mvn -s ./settings.xml"
  def mvnCmd = "mvn"

  stage('Checkout Source') {
    checkout scm
  }

  def groupId    = getGroupIdFromPom("pom.xml")
  def artifactId = getArtifactIdFromPom("pom.xml")
  def version    = getVersionFromPom("pom.xml")

  stage('Build war') {
    echo "Building version ${version}"

    sh "${mvnCmd} clean package -DskipTests"
  }
  stage('Unit Tests') {
    echo "Unit Tests"
    sh "${mvnCmd} test"
  }

  stage('Build OpenShift Image') {
    def newTag = "dev-${version}"
    echo "New Tag: ${newTag}"

    // Copy the war file and the configurations to deployments directory.
    sh "mkdir deployments"
    sh "cp -r configuration deployments"
    sh "cp ./target/tomcat-jdbc.war deployments/ROOT.war"

    // Start Binary Build in OpenShift using the file we just published
    // Add new tag to newly create Image.
    sh "oc project dev"
    sh "oc start-build sampleweb --follow --from-dir=./deployments -n dev"

    openshiftTag alias: 'false', destStream: 'sampleweb', destTag: newTag, destinationNamespace: 'dev', namespace: 'dev', srcStream: 'sampleweb', srcTag: 'latest', verbose: 'false'
  }

  stage('Deploy to Dev') {
    // Patch the DeploymentConfig so that it points to the latest dev-${version} Image.
    // Do deploy the target.
    sh "oc project dev"
    sh "oc patch dc sampleweb --patch '{\"spec\": { \"triggers\": [ { \"type\": \"ImageChange\", \"imageChangeParams\": { \"containerNames\": [ \"sampleweb\" ], \"from\": { \"kind\": \"ImageStreamTag\", \"namespace\": \"dev\", \"name\": \"sampleweb:dev-$version\"}}}]}}' -n dev"

    openshiftDeploy depCfg: 'sampleweb', namespace: 'dev', verbose: 'false', waitTime: '', waitUnit: 'sec'
    openshiftVerifyDeployment depCfg: 'sampleweb', namespace: 'dev', replicaCount: '1', verbose: 'false', verifyReplicaCount: 'false', waitTime: '', waitUnit: 'sec'
  }
}

// Convenience Functions to read variables from the pom.xml
def getVersionFromPom(pom) {
 	def matcher = readFile(pom) =~ '<version>(.+)</version>'
 	matcher ? matcher[0][1] : null
}
def getGroupIdFromPom(pom) {
 	def matcher = readFile(pom) =~ '<groupId>(.+)</groupId>'
 	matcher ? matcher[0][1] : null
}
def getArtifactIdFromPom(pom) {
 	def matcher = readFile(pom) =~ '<artifactId>(.+)</artifactId>'
 	matcher ? matcher[0][1] : null
}

