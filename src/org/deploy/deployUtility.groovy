#!groovy

package org.deploy 

class DeployUtility implements Serializable {
    def steps;
    // declare valiable

    DeployUtility(steps) {
        this.steps = steps
    }

    def initilizeEnvironmentProp() {
        // Initialize the properties of the deployment server with respect to different enviroment
        // and pre steps before deploying the artifact
    }

    def downloadArtifact() {
        // dowload the artifact were it was published
        // need to initialise this variable
        def username;
        def password;
        def url;
        steps.sh "wget --no-check-certificate --user=${username}" --password="${password} ${url}"
    }

    def deployArtifact() {
        // write the script to deploy the downloaded artifact to desired environment
    }
}