#!groovy​

pipeline {
agent any

options {
timeout(time: 1, unit: 'HOURS')
buildDiscarder(logRotator(numToKeepStr: '5', artifactNumToKeepStr: '5', daysToKeepStr: '7'))
    }

stages {
stage('Clone Repo') {
steps {
checkout scm
            }
        }
stage('Lint') {
steps {
sh 'make lint'
            }
        }

stage('Unit Tests') {
steps {
sh 'make unit-tests-all'
            }
        }

stage('Instrumentation Tests') {
steps {
sh 'make instrumentation-tests'
            }
        }

stage('E2E Tests') {
steps {
sh 'make e2e-tests'
            }
        }

stage('Smoke Tests') {
steps {
retry(2) {
sh 'make emulator-smoke'
                }
            }
        }

stage('Publish to Google Play Alpha') {
when {
expression { return env.BRANCH_NAME =~ /^release-*/ }
             }
environment {
KEY_ALIAS = credentials('keyAlias')
KEY_PASSWORD = credentials('keyPassword')
STORE_FILE = credentials('storeFile')
STORE_PASSWORD = credentials('storePassword')
FASTLANE_KEY = credentials('playStoreKey')
             }
steps {
lock('deploy') {
retry(2) {
sh 'make deploy-to-alpha'
                     }
                 }
             }
         }


    }
}
