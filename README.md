# jmxBossPoller
A simple java application that connects to a Standalone Wildfly 10 instance and polls for key metrics.

# Usage

./gradlew clean

./gradlew build

Notes: run with Java 8. This is not compatible with Java 11 and has not been tested with any other java version.

./jmxBossPoller.sh <seconds_to_sleep> <polling_iterations> <wildfly_host> <wildfly_user> <wildfly_password> <wildfly_jmx_port[default:9990]>
