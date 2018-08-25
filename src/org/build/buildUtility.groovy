#!groovy

package org.build 

class BuildUtility implements Serializable {
    def steps;

    BuildUtility(steps) {
        this.steps = steps
    }

    def buildArtifact() {
        // write the steps for building artifact using
        // steps.sh "enter the command"
    }

    def publishArtifact() {
        // write script to publish the builded artifact
    }

}