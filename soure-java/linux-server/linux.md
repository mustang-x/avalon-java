# Linux

## 命令相关

- **nohub**

  - 安装：

    ```:central_african_republic:
    # yum install coreutils		// 安装
    # vi .bash_profile 			// [用户]根目录 PATH 添加 :/usr/bin
    # source ~/.bash_profile    // 生效
    =============================
    # .bash_profile
    
    # Get the aliases and functions
    if [ -f ~/.bashrc ]; then
            . ~/.bashrc
    fi
    
    # User specific environment and startup programs
    
    PATH=$PATH:$HOME/bin:/usr/bin
    
    export PATH
    ```

  - **nohup command > myout.file 2>&1 &**：输出被重定向到myout.file文件中

  - **>/dev/null 2>&1**：

    - 1 表示stdout标准输出，系统默认值是1，所以">/dev/null"等同于"1>/dev/null"
    - 2 表示stderr标准错误
    - & 表示等同于的意思，2>&1，表示2的输出重定向等同于1



- **ps -ef | grep java**：查看进程
- **jps -v**：查看java进程参数

- **wget**： 
  **-no-check-certificate** : 表示不校验SSL证书，因为中间的两个302会访问https，会涉及到证书的问题，不校验能快一点，影响不大。  

  **-o** : 下载并以不同的文件名保存  

  **-c** : 使用wget断点续传  

  **-b** : 使用wget后台下载 tail -f wget-log 查看进度