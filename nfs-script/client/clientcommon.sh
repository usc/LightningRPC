# datatype
#  java     1
#  hessian  2
#  protobuf 3
#  kryo     4
#java -Xms4g -Xmx4g -Xmn1g -XX:+PrintGCDetails -XX:+PrintGCDateStamps -Xloggc:gc.log -Dwrite.statistics=true -Djava.ext.dirs="../../lib" $1 [serverip] [serverport] [concurrents] [timeout] [datatype] [requestsize] [runtime] [clientnums] > $2 2>&1 &
java -Xms4g -Xmx4g -Xmn1g -XX:+PrintGCDetails -XX:+PrintGCDateStamps -Xloggc:gc.log -Dwrite.statistics=true -Djava.ext.dirs="../../lib" $1 127.0.0.1 12200 100 1000 1 100 90 1  >> $2 2>&1 &
