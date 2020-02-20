#!/bin/sh

#set -x verbose #echo on

JMX_BOSS_POLLER_HOME="."
echo "JMX_BOSS_POLLER home: $JMX_BOSS_POLLER_HOME"

# **************************************************
# ** log config                                   **
# **************************************************

export LOGDIR=$JMX_BOSS_POLLER_HOME/log

CONFIG=config

CLASSPATH=$CLASSPATH:$CONFIG

# **************************************************
# ** get jar files                                **
# **************************************************

for f in `find $JMX_BOSS_POLLER_HOME/lib -type f -name "*.jar"` `find $JMX_BOSS_POLLER_HOME/lib -type f -name "*.zip"`
do
 CLASSPATH=$CLASSPATH:$f
done

#echo "classpath: $CLASSPATH"

# **************************************************
# ** get java opts                                **
# **************************************************

JAVA_OPTS="-Xms1024M -Xmx1024M -XX:MaxMetaspaceSize=256M -XX:+UseG1GC "

echo "java opts: $JAVA_OPTS"

# **************************************************
# ** exec job executor                            **
# **************************************************

java $JAVA_OPTS -cp $CLASSPATH boss.metrics.RunPoller -seconds $1 -iterations $2 -host $3 -user $4 -pass $5 -port $6
