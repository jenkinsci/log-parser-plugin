## [2.4.2](https://github.com/jenkinsci/log-parser-plugin/compare/v2.3.6...v2.4.2) (2025-05-10)


### Features

* **Timestamper:** Adding support for Timestamper ([dcce20a](https://github.com/jenkinsci/log-parser-plugin/commit/dcce20ad6bb80a7b04daa56a929feb1618da4901))
* upgrade to Jenkins LTS Core 2.462.3 for Java 11 support ([#150](https://github.com/jenkinsci/log-parser-plugin/issues/150)) ([5d5057e](https://github.com/jenkinsci/log-parser-plugin/commit/5d5057e0b52377af8e08d94f12f1a047c6eba280))


### Bug Fixes

* **Escpaing:** Remove xterm escape codes from log file ([1b40ade](https://github.com/jenkinsci/log-parser-plugin/commit/1b40ade9af7e6b6b9deb87cea6b4920866b9fb31))

## [2.3.6](https://github.com/jenkinsci/log-parser-plugin/compare/v2.3.5...v2.3.6) (2024-11-25)

## [2.3.5](https://github.com/jenkinsci/log-parser-plugin/compare/v2.3.4...v2.3.5) (2024-08-07)

## [2.3.4](https://github.com/jenkinsci/log-parser-plugin/compare/v2.3.3...v2.3.4) (2024-06-03)

## [2.3.3](https://github.com/jenkinsci/log-parser-plugin/compare/v2.3.2...v2.3.3) (2023-12-23)

## [2.3.2](https://github.com/jenkinsci/log-parser-plugin/compare/v2.3.1...v2.3.2) (2023-10-15)


### Bug Fixes

* **JENKINS-72048:** Correcting NPE ([16f069e](https://github.com/jenkinsci/log-parser-plugin/commit/16f069e6faea50630b4869337aa1703188b1e52c))

## [2.3.1](https://github.com/jenkinsci/log-parser-plugin/compare/d613f5ef8a166edc518f580165a8367746a463c0...v2.3.1) (2023-09-20)


### Features

* Introduce `StreamParsingStrategy` to support builds with large logs ([#40](https://github.com/jenkinsci/log-parser-plugin/issues/40)) ([6b4b6e6](https://github.com/jenkinsci/log-parser-plugin/commit/6b4b6e6c95a7da4eaf49d6fe4be937da95ffc11b))
* rebaseline Jenkins to 2.387.3 ([d6b4cc8](https://github.com/jenkinsci/log-parser-plugin/commit/d6b4cc8e995221d354fd4d5cf09a5787c23af73b))


### Bug Fixes

* Colors & hints on trend graphs do not match to the data ([413b828](https://github.com/jenkinsci/log-parser-plugin/commit/413b828a52855a6345f72e869ba803544393160e))
* docker-compose up doesn't support --rm ([d613f5e](https://github.com/jenkinsci/log-parser-plugin/commit/d613f5ef8a166edc518f580165a8367746a463c0))
* Jenkins Log Parser Debug Icon not shown  ([1e930e0](https://github.com/jenkinsci/log-parser-plugin/commit/1e930e00df73337ea75d8fe8d9caa38e3d7a792a))
* **Memory:** Correct potential OOM when parsing logs in workflows ([#36](https://github.com/jenkinsci/log-parser-plugin/issues/36)) ([bff7f9f](https://github.com/jenkinsci/log-parser-plugin/commit/bff7f9f53820aade452a4c44441bbfabc905931e)), closes [/github.com/jenkinsci/workflow-job-plugin/blob/1551f82/src/main/java/org/jenkinsci/plugins/workflow/job/WorkflowRun.java#L1105](https://github.com/jenkinsci//github.com/jenkinsci/workflow-job-plugin/blob/1551f82/src/main/java/org/jenkinsci/plugins/workflow/job/WorkflowRun.java/issues/L1105)
* Now Jenkins is available on port 8081 and boots ([f269c53](https://github.com/jenkinsci/log-parser-plugin/commit/f269c53fa0ddb5ae28ae3719afe6d6ee3ccc9d8e))
