# docker

## 命令

### 帮助命令

- docker version
- docker info
- docker help

### 镜像命令

- docker images：
  - -a：列出本地所有的镜像(中间映像层)
  - -q：镜像ID
  - --digests：镜像摘要信息
  - --no-trunc：完整的镜像信息

```htm
[root@izwz9ddfevb04g2s824qqlz ~]# docker images
REPOSITORY                 TAG                 IMAGE ID            CREATED             SIZE
x-tomcat                    0.0.1               b187d8eeb7f8        2 months ago        367 MB
docker.io/ubuntu            latest              cd6d8154f1e1        3 months ago        84.1 MB
docker.io/tomcat            7.0.77-jre8         0fd7cd6084f8        18 months ago       367 MB
docker.io/training/webapp   latest              6fae60ef3446        3 years ago         349 MB

```

- docker search [镜像名] ：查找镜像  | -s 30  STARS数大于30的
- docker pull [镜像名:TGA] ：拉取镜像 默认最新
- **docker rmi [镜像名:TGA]** ：删除 *ps:容器的是rm*

### 容器命令

- **dockers run [option]  镜像名 | 镜像ID**
  - --name：为运行容器指定名称
  - **-d**：后台运行，并返回容器ID
  - -it：以交互模式运行容器，并未容器分配一个伪输入终端
  - -P[大写]：随机端口映射
  - **-p[小写]**：指定端口映射
    - **ip:hostPort:containerPort**
    - ip::containerPort
    - **hostPort:containerPort** ：外部端口[Docker]：内部端口
    - containerPort
- **docker ps**
  - -l ：上一个运行容器
  - -n [num] ：上num次运行的容器
  - **-q** ：容器ID
  - -a ：所有容器
- docker start | stop [CONTAINER ID | NAMES] ：启动 | 关闭 容器
- **docker inspect** ：查看容器信息
- **docker  exec | attach** ：进入容器
  - **docker exec -it [容器名 | 容器ID] /bin/sh**
- **docker cp 容器ID:容器文件内路径  目的主机路径： 文件数据拷贝**

------

## Docker 镜像

- 联合文件加载系统
- Docker加载原理：bootfs / rootfs
- docker commit  [命名空间/镜像名:TGA]



------

## Docker 容器数据卷

### 数据卷 

- 命令：**docker run  -it -v /宿主机绝对路径目录 : /内容目录 镜像名**

  `docker run  -it -v /dataVolume:/containerDataVolume centos`

  使用 `docker inspect` 查看：

  ```:baby_bottle:
  "HostConfig": {
              "Binds": [
                  "/dataVolume:/containerDataVolume"
              ],
  
  ```


- 使用`dockerfile`文件

  ```html
  # volume test
  FROM centos
  VOLUME ["/containerData1", "/containerData2"]
  CMD echo "finished----------------success"
  CMD /bin/bash
  
  ```

- **--volumes-from 容器ID | 容器名** ：容器之间数据卷共享；配置信息也会传递



## DockerFile

