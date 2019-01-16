require.config({
    baseUrl: "js",
    paths: {
        "jquery": "jquery-3.3.1.min",
        "Util": "Util",
        "Device": "Device"
    }
});

require(["jquery", "Util", "Device"], ($, Util, Device)=>{
    //首先保存默认值
    document.querySelectorAll("table > tbody > tr").forEach((tr, key) => {
        tr.children[2].dataset.defaultValue = tr.children[2].innerText.toString().trim();
    });

    let ld_list = document.querySelector("#ludeng_list");
    ld_list.removeChild(ld_list.children[0]);
    let deviceIdNames = window.localStorage.getItem("deviceIdNames");
    let currentDevice = document.querySelector("#currentDevice");
    let util = new Util();
    deviceIdNames = JSON.parse(deviceIdNames);
    deviceIdNames.forEach((idName, index) => {
        let deviceId = idName.id;
        let deviceName = idName.name;
        let node = document.createElement('div');
        node.innerHTML = `<a class="dropdown-item" href="javascript:;" data-device-id='${deviceId}'>${deviceName}</a>`;
        node.children[0].addEventListener("click", event => {
            if(currentDevice.dataset.deviceId !== deviceId.toString()){
                currentDevice.dataset.deviceId = deviceId.toString();
                currentDevice.innerText = `${deviceName}`;
                let list = document.querySelectorAll("table > tbody > tr");
                let device = new Device(deviceId);
                list.forEach((tr, key) => {
                    if(!deviceId || !$.isNumeric(deviceId)){
                        throw "设备Id为空";
                    }
                    device.update(tr.children[2].id).then(data=>{
                        util.notify("更新成功", 2);
                    }, ()=>{
                        tr.children[2].innerText = tr.children[2].dataset.defaultValue;
                        util.notify("更新失败", 2);
                    })/*.catch(error=>{
                        tr.children[2].innerText = tr.children[2].dataset.defaultValue;
                        util.notify("更新失败", 2);
                    })*/;
                });
            }else{
                util.notify("设备Id未变", 2);
            }
        });
        ld_list.appendChild(node.children[0]);
    });
    currentDevice.dataset.deviceId = ld_list.children[0].dataset.deviceId;
    currentDevice.innerText = ld_list.children[0].innerText;

    //为气象站添加打开关闭事件
    document.querySelector(`#i3201_0_5551_cmd`).addEventListener("click", function(){
        let currentDevice = document.querySelector("#currentDevice");
        let device = new Device(currentDevice.dataset.deviceId);
        let util = new Util();
        let currentItem = document.querySelector(`#i3201_0_5551`);
        if(this.innerText === "打开"){//气象站
            device.execute("003").then(data => {
                currentItem.innerText = "开";
                this.innerText = "关闭";
            }, ()=>{
                util.notify(`打开气象站失败`, 2);
            });
        }else{
            device.execute("004").then(data => {
                currentItem.innerText = "关";
                this.innerText = "打开";
            }, ()=>{
                util.notify(`关闭气象站失败`, 2);
            });
        }
    });

    //为屏幕添加打开关闭事件
    document.querySelector(`#i3201_1_5551_cmd`).addEventListener("click", function(){
        let currentItem = document.querySelector(`#i3201_1_5551`);
        let currentDevice = document.querySelector("#currentDevice");
        let device = new Device(currentDevice.dataset.deviceId);
        let util = new Util();
        if(this.innerText === "打开"){//屏幕
            device.execute("001").then(data => {
                currentItem.innerText = "开";
                this.innerText = "关闭";
            }, ()=>{
                util.notify(`打开屏幕失败`, 2);
            });
        }else{
            device.execute("002").then(data => {
                currentItem.innerText = "关";
                this.innerText = "打开";
            }, ()=>{
                util.notify(`关闭屏幕失败`, 2);
            });
        }
    });

    //添加等待动效
    function addWaiting(id){
        let refreshId = `#${id}_refresh`;
        let selectId = `#${id}_select`;
        let waitingId = `#${id}_waiting`;
        let refresh = document.querySelector(refreshId);
        let select = document.querySelector(selectId);
        let waiting = document.querySelector(waitingId);
        let currentDevice = document.querySelector("#currentDevice");
        refresh.addEventListener("click", event => {
            let device = new Device(currentDevice.dataset.deviceId);
            let util = new Util();
            select.disabled = true;
            waiting.classList.remove("hide");
            device.update(id).then(data=>{
                util.notify("刷新成功", 2);
            }, ()=>{
                util.notify("刷新失败", 2);
            }).catch(error=>{
                console.log(error);
            }).finally(()=>{
                waiting.classList.add("hide");
                select.disabled = false;
            });
        });
    }
    document.querySelectorAll("table > tbody > tr").forEach((tr, key) => {
        addWaiting(tr.children[2].id);
    });

    //添加全部刷新点击事件
    document.querySelector("#refreshAll").addEventListener("click", event=>{
        let currentDevice = document.querySelector("#currentDevice");
        let device = new Device(currentDevice.dataset.deviceId);
        let util = new Util();
        event.target.classList.add("fa-spin");
        device.updateAll().then(data =>{
            util.notify("刷新成功", 2);
        },()=>{
            util.notify("刷新失败", 2);
        }).finally(()=>{
            event.target.classList.remove("fa-spin");
        });
        event.stopPropagation();
    });

    let device = new Device(currentDevice.dataset.deviceId);
    device.updateAll();
});