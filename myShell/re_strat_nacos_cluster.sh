#!/bin/bash
#关闭原来的nacos服务器
sh /etc/nacos-cluster-1/bin/shutdown.sh
sh /etc/nacos-cluster-2/bin/shutdown.sh
#设置新服务器的端口号
portOne=8848
portTwo=8948
#获取本地ip地址
ip=`ifconfig -a|grep inet|grep -v 127.0.0.1|grep -v inet6|awk '{print $2}'|tr -d "addr:"​`
str="$ip:$portOne\n$ip:$portTwo"
#输出cluster文件
echo $str > /private/etc/nacos-cluster-1/conf/cluster.conf
echo $str > /private/etc/nacos-cluster-2/conf/cluster.conf
#重启nacos cluster
sh /etc/nacos-cluster-1/bin/startup.sh
sh /etc/nacos-cluster-2/bin/startup.sh
