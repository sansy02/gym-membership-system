# 健身房会员管理系统 — 网页版 (Gym Membership System — Web Version)

期末大作业参考项目：Java OOP + JDBC + MySQL + Web 前端

## 项目结构

```
GymWebSystem/
├── run.bat                    # 一键启动脚本（双击运行）
├── sql/
│   └── setup.sql              # 数据库建表 + 初始数据
├── lib/
│   └── mysql-connector-j-8.4.0.jar   # MySQL JDBC 驱动
├── web/                       # 前端（HTML + CSS + JS，无框架）
│   ├── index.html             # 登录页 + 主界面（单页应用）
│   ├── style.css              # 样式
│   └── app.js                 # 前端逻辑（fetch 调用后端 API）
├── out/                       # 编译输出（class 文件）
└── src/gym/
    ├── Main.java              # 入口：启动 HTTP 服务器
    ├── database/
    │   └── DBConnection.java  # 数据库连接（改密码在这里）
    ├── model/                 # OOP 模型层（与桌面版相同）
    │   ├── IPayable.java          # 接口
    │   ├── Person.java            # 抽象类（父）
    │   ├── Member.java            # extends Person + implements IPayable
    │   ├── Staff.java             # 抽象类 extends Person
    │   ├── Trainer.java           # extends Staff（多层继承）
    │   ├── Receptionist.java      # extends Staff（多层继承）
    │   ├── MembershipPlan.java    # 抽象类（父）
    │   ├── BasicPlan.java         # extends MembershipPlan
    │   ├── PremiumPlan.java       # extends MembershipPlan
    │   ├── VIPPlan.java           # extends MembershipPlan
    │   ├── Payment.java           # 支付记录
    │   └── Attendance.java        # 签到记录
    └── server/                # 后端 HTTP 服务器（纯标准库）
        ├── MiniHttpServer.java    # HTTP 服务器 + 静态文件服务
        ├── ApiHandler.java        # API 路由 + 数据库操作
        └── JsonUtils.java         # 极简 JSON 解析/生成
```

## 运行步骤

### 1. 启动 MySQL（XAMPP）
打开 XAMPP Control Panel → 点击 **MySQL** 旁的 **Start** 按钮。

### 2. 创建数据库
- 浏览器打开 `http://localhost/phpmyadmin`
- 点击 **SQL** 标签 → 粘贴 [sql/setup.sql](sql/setup.sql) 的内容 → 点 **Go**
- 数据库 `gym_db` 和初始数据就建好了

### 3. 修改数据库密码（如果需要）
编辑 [DBConnection.java](src/gym/database/DBConnection.java)：
- XAMPP 的 MySQL 默认 root 密码为**空**
- 把 `PASSWORD = "1234"` 改成 `PASSWORD = ""`
- 改完重新编译：双击运行 `compile.bat` 或执行下方编译命令

### 4. 启动系统
双击 [run.bat](run.bat) → 浏览器打开 **http://localhost:8080**

## 登录账号

| 用户名 | 密码 | 角色 |
|--------|------|------|
| admin | admin123 | Receptionist |
| trainer1 | 123456 | Trainer |

## API 接口一览

| 方法 | 路径 | 功能 |
|------|------|------|
| POST | /api/login | 登录验证 |
| GET | /api/stats | 仪表盘统计 |
| GET | /api/plans | 计划列表 |
| GET | /api/members | 会员列表（Read） |
| POST | /api/members | 添加会员（Create） |
| PUT | /api/members | 更新会员（Update） |
| DELETE | /api/members?member_id=X | 删除会员（Delete） |
| GET | /api/payments | 支付列表 |
| POST | /api/payments | 添加支付 |
| GET | /api/attendance | 签到列表 |
| POST | /api/attendance | 执行签到 |

## OOP 概念对照（评分点）

| 概念 | 代码位置 |
|------|---------|
| 封装 Encapsulation | 所有模型类私有属性 + getter/setter |
| 继承 Inheritance | Person → Member；Person → Staff → Trainer/Receptionist；MembershipPlan → 3 个子类 |
| 多态 Polymorphism | getRole() / displayInfo() / getPlanDetails() 重写 |
| 抽象 Abstraction | Person、Staff、MembershipPlan 抽象类 |
| 接口 Interface | IPayable（Member 实现） |
| 异常处理 Exception Handling | ApiHandler 全局 try-catch + 错误响应 |
