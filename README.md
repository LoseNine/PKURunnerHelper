# PKURUNNER-Helper
PKURUNNER助手安卓版客户端

### 改写自学长的项目https://github.com/fieryd/PKURunningHelper
### 将Python程序改写成安卓移动应用

# screenshot
![](https://github.com/LoseNine/PKURunnerHelper/blob/master/imgs/1.png)
![](https://github.com/LoseNine/PKURunnerHelper/blob/master/imgs/2.png)
![](https://github.com/LoseNine/PKURunnerHelper/blob/master/imgs/3.png)

### 注意：由于我有些懒惰，不想改这个项目了
### 我会把Python版本的关键参数放一下
### 2021-03-25修改了abstract参数
### 使用Python版本，只需要加上参数
'abstract':str(hashlib.sha256(f'{self.studentID}_{record.date}_781f5106781f5106'.encode()).hexdigest()[:32])
