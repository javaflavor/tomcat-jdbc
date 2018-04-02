#!groovy

node('maven') {
	//def mvnCmd = "mvn -s ./settings.xml"
	def mvnCmd = "mvn"
	
	def appName = "sampleweb"
	def devPrj = "dev"
	
	stage('Cleanup Dev env') {
		// Delete all objects except for is.
		openshift.withCluster() {
			openshift.withProject(devPrj) {
			    openshift.selector("bc", [ app: appName ]).delete()
			    openshift.selector("dc", [ app: appName ]).delete()
			    openshift.selector("svc", [ app: appName ]).delete()
			    openshift.selector("pod", [ app: appName ]).delete()
			    openshift.selector("route", [ app: appName ]).delete()
			}
		}
	}

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
				// Create buildConfig from file "openshift/sampleweb-bc.yaml".
			    openshift.create(readFile("openshift/sampleweb-bc.yaml"))
			    // Start image build.
				openshift.selector("bc", appName).startBuild("--from-dir=./deployments").logs("-f")
				// Tag created image.
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
			    def created = openshift.newApp("--name=$appName", "$devPrj/$appName:$newTag")
				
				//
				// openshiftDeploy depCfg: appName, namespace: devPrj, verbose: 'false', waitTime: '', waitUnit: 'sec'
				// openshiftVerifyDeployment depCfg: appName, namespace: devPrj, replicaCount: '1', verbose: 'false', verifyReplicaCount: 'false', waitTime: '', waitUnit: 'sec'
				// 
				// Rollout latest.
//				def dc = openshift.selector("dc", appName);
				
				//dc.rollout().latest()
				
//				def result = dc.rollout().status()
//				echo "rollout status: ${result}"

				// Expose service.
				created.narrow("svc").expose()

				// Wait and print status deployment.
				def dc = created.narrow("dc")
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

