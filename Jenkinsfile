#!groovy

node('maven') {
	//def mvnCmd = "mvn -s ./settings.xml"
	def mvnCmd = "mvn"
	
	def appName = "sampleweb"
	def devPrj = "dev"

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

	def newTag = "dev-${version}"

	stage('Build OpenShift Image') {
		echo "New Tag: ${newTag}"

		// Copy the war file and the configurations to deployments directory.
		sh "mkdir deployments"
		sh "cp -r configuration deployments"
		sh "cp ./target/tomcat-jdbc.war deployments/ROOT.war"

		// Start Binary Build in OpenShift using the file we just published
		// Add new tag to newly create Image.
		// 
		// sh "oc project ${devPrj}"
		// sh "oc start-build ${appName} --follow --from-dir=./deployments -n ${devPrj}"
		// sh "oc tag $appName:latest $appName:$newTag"
		// 
		openshift.withCluster() {
			openshift.withProject(devPrj) {
				openshift.selector("bc", appName).startBuild("--from-dir=./deployments").logs("-f")
				def result = openshift.tag("$appName:latest", "$appName:$newTag")
				echo "${result.actions[0].cmd}"
				echo "${result.actions[0].out}"
			}
		}
	}

	stage('Deploy to Dev') {
		// Patch the DeploymentConfig so that it points to the latest dev-${version} Image.
		// Do deploy the target.
		//
		openshift.withCluster() {
			openshift.withProject(devPrj) {
				//
				// sh "oc project ${devPrj}"
				// sh "oc patch dc ${appName} --patch '{\"spec\": { \"triggers\": [ { \"type\": \"ImageChange\", \"imageChangeParams\": { \"containerNames\": [ \"$appName\" ], \"from\": { \"kind\": \"ImageStreamTag\", \"namespace\": \"$devPrj\", \"name\": \"$appName:dev-$version\"}}}]}}' -n $devPrj"
				//
				def patch = openshift.selector("dc", appName).object()
				patch.spec.triggers[0].imageChangeParams.from.name = "$appName:$newTag"
				echo "patched spec.triggers: ${patch.spec.triggers[0]}"
				openshift.apply(patch)
				
				//
				// openshiftDeploy depCfg: appName, namespace: devPrj, verbose: 'false', waitTime: '', waitUnit: 'sec'
				// openshiftVerifyDeployment depCfg: appName, namespace: devPrj, replicaCount: '1', verbose: 'false', verifyReplicaCount: 'false', waitTime: '', waitUnit: 'sec'
				// 
				// Rollout latest.
				def dc = openshift.selector("dc", appName);
				dc.rollout().latest()
				
				// Wait and print status deployment.
				dc.rollout().status("-w")
			}
		}
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

