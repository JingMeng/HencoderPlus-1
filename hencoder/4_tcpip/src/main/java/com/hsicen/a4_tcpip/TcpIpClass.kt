package com.hsicen.a4_tcpip

import java.net.InetSocketAddress
import java.net.Socket
import java.net.URL

/**
 * 作者：hsicen  2020/4/7 18:21
 * 邮箱：codinghuang@163.com
 * 功能：
 * 描述：Tcp/Ip 协议族   HTTPS的建立过程
 */
class TcpIpClass {

  // TCP/IP 协议族
  /**
   * 概念：一系列协议组成的一个网络分层模型
   * 分层：网络的不稳定性，各层功能独立，模块封装，通用
   *
   * 四层网络分层模型：
   *     应用层(Application Layer)：HTTP，FTP(文件传输)，DNS(网络寻址)
   *     传输层(Transport Layer)：TCP，UDP  数据分块传输策略，数据组装
   *     网络层(Internet Layer)：IP  网络寻址，以包为单位发送网络数据
   *     数据链路层(Link Layer)：以太网，Wi-Fi
   *
   * TCP连接：
   *     什么叫连接：通信双方建立确认可以通信，不会将对方的消息丢弃，即为建立连接，实质是建立一个Socket(host,端口)
   *     TCP连接的建立：三次握手，第一次(A->B我要给你发消息了)，第二步(B->A好的，我知道了，我也要给你发消息)，第三步(A->B好的，我知道了)
   *     TCP连接的关闭：四次挥手，第一次(A->B我没有消息要给你发了)，第二次(B->A好的，我知道了)，第三次(B->A我没有消息要给你发了)，第四步(A->B好的，我知道了)
   *
   * 长连接：因为移动网络并不在公网Internet中，而是在运营商的内网中，并不具有正真的公网ip；因此，当某个TCP连接在一段时间内不通信后，
   *        网关会出于网络性能考虑而关闭这条TCP连接和公网的连接通道，导致这个TCP端口不能再收到外部通信的消息，即TCP连接被关闭了
   * 长连接的实现：心跳连接，让网关不关闭这条连接
   */


  //HTTPS 连接的建立过程
  /**
   * 定义：HTTP over SSL 的简称，即工作在SSL/TLS 上的HTTP，说白了就是加密通信的HTTP
   * HTTPS和SSL的关系就像当初HTTP和HTML的关系，后台SSL不仅可以实现HTTPS的安全，作为一层独立的协议，还可以作为FTP的底层加密传输
   * 后面SSL -> TLS
   * TLS作为安全层，在HTTP(应用层)和TCP(传输层)之间，对数据进行加密和解密； HTTP+TLS -> HTTPS
   *
   * 工作原理：在客户端和服务器之间利用非对称加密协商出一套对称密钥，每次发送信息之前先将内容加密，收到之后解密，达到内容的加密传输
   *
   * HTTPS的建立过程：
   *    1.客户端发送一个字节的数据，称为 Client Hello
   *      附加信息：
   *          可选 TLS 版本集合
   *          可选 Cipher Suite 加密套件集合
   *          客户端随机数(客户端生成，随机，唯一，不受环境影响)
   *
   *    2.服务器发送一个字节的数据，称为 Server Hello
   *      附加信息：
   *          选中的TLS版本
   *          选中的Cipher Suite加密套件
   *          服务器随机数(服务器生成，随机，唯一，不受环境影响)
   *
   *    3.服务器发送证书(主要是服务器公钥和主机名的验证)
   *    [ 服务器公钥(用来加密数据)
   *      服务器的名字
   *      服务器的地区
   *      服务器的主机名 ] -> 服务器证书
   *    [ [服务器公钥]的签名(该签名的私钥不是上面那个公钥对应的私钥) ] -> 服务器证书签名
   *
   *    [ 证书签发机构的公钥(用户验证 [服务器公钥的签名] 的公钥)
   *      证书签发机构的名字
   *      证书签发机构地区 ] -> 签发机构证书
   *    [ [证书签发机构的公钥]的签名 ] -> 签发机构证书签名
   *
   *    [ 证书签发机构的签发机构的公钥([证书签发机构的公钥的签名]的公钥) ->根证书机构的公钥  利用根证书来验证服务器证书的合法性
   *      根证书机构的名字
   *      根证书机构的地区 ] -> 根证书(可信的签发机构)
   *    最终只为验证 (服务器公钥+服务器主机名) 的合法性
   *
   *    4.客户端发送Pre-master Secret (随机数)
   *      利用服务器公钥加密发送(非对称加密发送)
   *
   *      然后服务器和客户端会利用（客户端随机数+服务器随机数+Pre-master Secret）算出一个[Master Secret]
   *      再利用[Master Secret]计算出：客户端加密密钥，服务器加密密钥，客户端MAC Secret，服务器MAC Secret
   *      客户端利用客户端加密密钥加密消息发送给服务器，利用服务器加密密钥解密服务器返回的数据
   *      服务器利用客户端加密密钥解密客户端发送的消息，利用服务器加密密钥加密返回给客户端消息
   *      设计客户端和服务器加密密钥的原因是防止消息被别人截获恶意扔回来
   *
   *      HMAC：基于Hash的消息认证码(带有密钥的Hash算法)，数据hash过后，并把密钥(MAC Secret)参合进去
   *
   *    5.客户端通知：将使用加密通信
   *    6.客户端发送：Finished (将上面5步的内容加密，然后HMAC，发送给服务器)
   *    7.服务器通知：将使用加密通信
   *    8.服务器发送：Finished (将上面7步的内容加密，然后HMAC，发送给客户端)
   *
   * Android中使用HTTPS
   *     正常情况：服务器有签发机构签发的证书，直接访问
   *     需要自己写证书验证场景：
   *         1.使用的是自签名证书 (大学内网自签名证书)
   *         2.证书信息不全，缺乏中间证书机构
   *         3.手机操作系统较旧，没有安装最新加入的根证书
   */

  fun httpsExample() {
    val url = URL("https://www.baidu.com/")
    val urlConnection = url.openConnection()
    val inStream = urlConnection.getInputStream()
    val outStream = urlConnection.getOutputStream()

    val socket = Socket("http://www.baidu.com", 80)
    socket.connect(InetSocketAddress(80))
    socket.getInputStream()     //收消息
    socket.getOutputStream()    //发消息
  }
}
