#打造Android不死进程


##为什么要进程保活？

应用为什么要在后台一直活着？他活着做什么？

想必所有的应用都想在后台不死，一旦应用不死，后台就可以向应用推送消息，让用户看到，其实大多数应用都只是为了能够收到后台的推送而已，这只是适用的一个小场景，但是大家都知道，应用的推送一般都是第三方或者自己做的，而不是用的谷歌本身的推送，不像苹果，使用的是自己原生的推送，因此苹果不担心推送的问题，然而Android就不一样了，中国访问不了谷歌，所以原生推送自然也就用不了。

在国内手机厂商中，也有许多厂商做了只属于自己的推送，比如小米，华为，他们都有自己的一套推送系统，但是这毕竟是少数，大部分都用的是第三方推送，比如个推，极光等，还有一种就是大公司自己做的一套属于自己的推送，小公司一般都是不会自己做的，原因大家都懂的。

###为什么会有保活

国内的手机系统都是在Android基础上定制的，也就是说，系统被人家改过了，所以，各个手机厂商对于手机的资源分配都有不同的方式和标准。

但是一般情况下都是大致一样了，你想一下，如果一款手机中的所有应用都在后台一直运行，并且用户根本没有用到，这个时候还要占着手机的资源，那么手机自然也就很卡了，所以手机厂商对这一块要求还是都比较严格的。

一般如果一款应用一直在后台运行着，没有被用户放到前台，这个时候系统会自动的回收资源，开始一个一个的杀死进程，这个时候所有应用都难逃被杀的命运！但是我们看市场上的应用，比如微信，QQ，为什么怎么都不被手机杀死，其实微信这些应用可以说是系统级别的应用了，不再单单的只是一款应用，所以，微信不能死，如果你能把软件做到系统级别的，你也可以永久不死。

###保活的场景

大家都知道，手机对于资源的监控是很严格的，所以我们也不应该一直常驻后台，我们只是为了完成我们在后台的任务时尽量保证不被系统杀死就行了，下面是保活场景：

> 1. 即时通讯
> 2. 后天推送
> 3. 后台计步
> 4. 获取gps

###国内案例

国内，这块做的最好的就是咕咚了，你会发现，只要你使用咕咚点击了开始运动之后，他就再也死不了了，这是非常经典的案例，此时做进程保活是非常有必要的，因为用户一旦开始运动之后，他会直接将屏幕关闭，然后听着歌曲来跑步，那么这个时候就有问题了，手机一般只要用户主动的关闭屏幕之后，过一会就开始进入系统休眠状态，也就是开始清理后台程序了，那么一旦你的应用被清理，当用户再次进入程序的时候，发现跑了10公里，居然没有数据了，软件直接死掉了，你说他是不是会骂娘。

所以这个时候，就应该使用应用保活，保证应用不被系统杀死，而且被杀死后，能够重启。

咕咚的运动结束后，你会发现，他和其他的应用是一样的，很快就会被系统杀死，也再也不会自动重启，这是咕咚做的比较优秀的地方。

当然我也希望，我们做应用保活，只是为了用户好，而不是为了自己，在后台大占手机资源。

##打造不死应用

当我们需要进行应用保活时，一方面，是为了让应用尽量不要被系统杀死，一方面是为了自己的应用被系统杀死之后，能够自动重启，然后继续收集上次未收集完的数据

###进程的优先级

Android 系统将尽量长时间地保持应用进程，但为了新建进程或运行更重要的进程，最终需要清除旧进程来回收内存。 为了确定保留或终止哪些进程，系统会根据进程中正在运行的组件以及这些组件的状态，将每个进程放入“重要性层次结构”中。 必要时，系统会首先消除重要性最低的进程，然后是清除重要性稍低一级的进程，依此类推，以回收系统资源。

###利用 Activity 提升权限

方案设计思想：监控手机锁屏解锁事件，在屏幕锁屏时启动1个像素的 Activity，在用户解锁时将 Activity 销毁掉。注意该 Activity 需设计成用户无感知。

通过该方案，可以使进程的优先级在屏幕锁屏时间由4提升为最高优先级1。

*方案适用范围：*

> 1. 适用场景：本方案主要解决第三方应用及系统管理工具在检测到锁屏事件后一段时间（一般为5分钟以内）内会杀死后台进程，已达到省电的目的问题。
> 2. 适用版本：适用于所有的 Android 版本。

###利用 Notification 提升权限

方案设计思想：Android 中 Service 的优先级为4，通过 setForeground 接口可以将后台 Service 设置为前台 Service，使进程的优先级由4提升为2，从而使进程的优先级仅仅低于用户当前正在交互的进程，与可见进程优先级一致，使进程被杀死的概率大大降低。

方案实现挑战：从 Android2.3 开始调用 setForeground 将后台 Service 设置为前台 Service 时，必须在系统的通知栏发送一条通知，也就是前台 Service 与一条可见的通知时绑定在一起的。

通过实现一个内部 Service，在 WatchDogService 和其内部 Service 中同时发送具有相同 ID 的 Notification，然后将内部 Service 结束掉。随着内部 Service 的结束，Notification 将会消失，但系统优先级依然保持为2。

方案适用范围：适用于目前已知所有版本。

##进程死后拉活

我们要做的不仅仅是进程保活，一旦应用被系统杀死后，应用也就再也起不来了，那么数据也就不能继续采集了，此时候我们需要在他死之后，还能自动复活，然后继续使用

###利用系统广播拉活

方案设计思想：在发生特定系统事件时，系统会发出响应的广播，通过在 AndroidManifest 中“静态”注册对应的广播监听器，即可在发生响应事件时拉活。

常用的用于拉活的广播事件包括：

> 1. 开机广播
> 2. 网络变化
> 3. 文件挂载
> 4. 屏幕亮灭
> 5. 锁屏解锁
> 6. 应用安装卸载

方案适用范围：适用于全部 Android 平台。但存在如下几个缺点：

1. 广播接收器被管理软件、系统软件通过“自启管理”等功能禁用的场景无法接收到广播，从而无法自启。
2. 系统广播事件不可控，只能保证发生事件时拉活进程，但无法保证进程挂掉后立即拉活。

这种方案，只能用做备用方案。

###利用第三方应用广播拉活

通过反编译第三方 Top 应用，如：手机QQ、微信、支付宝、UC浏览器等，以及友盟、信鸽、个推等 SDK，找出它们外发的广播，在应用中进行监听，这样当这些应用发出广播时，就会将我们的应用拉活。

###利用系统Service机制拉活

方案设计思想：将 Service 设置为 START-STICKY，利用系统机制在 Service 挂掉后自动拉活

如下两种情况无法拉活:

> 1. Service 第一次被异常杀死后会在5秒内重启，第二次被杀死会在10秒内重启，第三次会在20秒内重启，一旦在短时间内 Service 被杀死达到5次，则系统不再拉起。
> 2. 进程被取得 Root 权限的管理工具或系统工具通过 forestop 停止掉，无法重启。

###利用 JobScheduler, AlarmManager 机制拉活

系统在 Android5.0 以上版本提供了 JobScheduler 接口，系统会定时调用该进程以使应用进行一些逻辑操作。

使用 JobScheduler, Android 系统能自动拉起被 Force Stop 的 Package，而 AlarmManager 无法拉起.Android 4.4 及以下版本使用 AlarmManager

使用定时 Observable : 避免 Android 定制系统 JobScheduler / AlarmManager 唤醒间隔不稳定的情况

###利用账号同步机制拉活

方案设计思想：Android 系统的账号同步机制会定期同步账号进行，该方案目的在于利用同步机制进行进程的拉活。

该方案需要在 AndroidManifest 中定义账号授权与同步服务。

方案适用范围：该方案适用于所有的 Android 版本，包括被 forestop 掉的进程也可以进行拉活。

###其他拉活方案

1. 利用系统通知管理权限进行拉活
2. 利用辅助功能拉活，将应用加入厂商或管理软件白名单。

这2个方案都亲自测试过，还是可以保活且可以拉活的，但是用户感知比较强，需要用户授权，要想让用户授权还是有难度的，毕竟需要你弹框引导用户，但是你要知道，很多用户没有那个耐心的，所以该方案虽然可行，但是很难实现我们想要的模样

##最后说明

以上的应用保活以及应用拉活方案，都已经实现，效果还算不错，但是有的手机没有测试过，大家可以借鉴，希望只是学习。













