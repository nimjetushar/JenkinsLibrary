#!groovy

package org.setup 

class SetupUtility implements Serializable {
    def steps;
    def gitUrl;
    def currentBranch;

    SetupUtility(steps) {
        this.steps = steps;
    }

    def init(env, params) {

        // Initialize env variables if required based on different conditions

        // Initialize credentails if required
        def credId = "demo";
        steps.withCredentials([
            [$class: 'UsernamePasswordMultiBinding', credentialsId: "${credId}",
                usernameValiable: 'UserName', passwordValiable: 'Password'
            ]
        ]) {
            steps.sh 'echo uname=$UserName pwd=$Password'
            // can initialize the username and password in any required valiable
            env.username = "${env.UserName}"
            env.password = "${env.Password}"
        }

        // setup tools
        // can initialize any version of tool as per availability and requirement
        def mvnHome = steps.tool '3.3.3_Linux'
        def java = steps.tool '1.8.0.65_Linux'
        env.JAVA_HOME = "${java}"
        env.NODEJS_HOME = "${steps.tool '6.9.5_NODEJS_Linux'}"

        // initialize path 
        env.PATH = "${java}/bin:${mvnHome}/bin:${env.PATH}/${env.NODEJS_HOME}/bin:${env.PATH}"
    }

    def gitInit(env) {
        steps.withCredentials([
            [$class: 'UsernamePasswordMultiBinding', credentialsId: "git_username_passowrd_key",
                usernameValiable: 'Git_username', passwordValiable: 'Git_password'
            ]
        ]) {
            def url = steps.sh(returnStdout: true, script: 'git config remote.origin.url').trim()
            steps.echo url
            def splitArr = url.tokenize('://')

            gitUrl = "https://$Git_username:$Git_password/${splitArr[1]}/${splitArr[2]}/${splitArr[3]}"

            // gets all available brances in repo
            def branches = steps.sh(returnStdout: true, script: 'git branch -r').trim();
            steps.echo branches
        }
    }

    def codeSetup(env) {
        // fails check for any condition before proceeding
        // if(env.BUILD_TYPE == null) {
        //     steps.error 'Build type not present'
        // }

        // initialize this manually or dynamically using script
        currentBranch = "demo"

        steps.sh "git fetch ${gitUrl}"
        steps.sh "git branch"
        steps.sh "git checkout origin/${currentBranch}"
        steps.sh "git checkout ${currentBranch}"
        steps.sh "git pull ${gitUrl} ${currentBranch}"
        steps.sh "git status"
        steps.sh "git branch"
    }

    def gitCommit(env) {
        steps.sh 'git commit pom.xml -m "add custom msg with some unique identifier"'
        steps.sh "git pull ${gitUrl} ${currentBranch}"

        // if required can also add tag at this level

        steps.sh "git push ${gitUrl}"
    }

    def addTag() {
        // write custom logic to add tag name in this case pom version is specified as tag name
        def pomFile = steps.readFile('./pom.xml').trim();
        def split1 = pomFile.split('<version>')
        def split2 = split1[1].split('</version>')
        def tagName = split2[0];

        steps.sh "git tag ${tagName}"
        steps.sh "git push ${gitUrl} --tags"
    }

    // this identifies the if the resent commit is from jenkins job or not
    def getCommitMsg() {
        def msg = steps.sh(returnStdout: true, script: 'git log --format=online -n 1').trim();
        if (msg.contains("unique identifier add in commit")) {
            steps.sh "echo skipping build"
            steps.error "skiping build"
        }
    }

    def sendMail() {
        def to = emailextrecipients([
            [$class: 'CulpritsRecipientProvider'],
            [$class: 'DevelopersRecipientProvider'],
            [$class: 'RequesterRecipientProvider']
        ])
        if (to != null && !to.isEmpty()) {
            def mailId = "test@test.com"
            steps.mail to: '$(mailId)', subject: "test build", body: "demo"
        }
    }
}