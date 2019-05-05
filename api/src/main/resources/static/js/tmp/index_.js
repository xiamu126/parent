(function(){
    let ld_list = document.querySelector("#ludeng_list");
    ld_list.removeChild(ld_list.children[0]);
    let deviceIdNames = window.localStorage.getItem("deviceIdNames");
    let currentDevice = document.querySelector("#currentDevice");
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
                list.forEach((tr, key) => {
                    if(!deviceId || !$.isNumeric(deviceId)){
                        throw "当前设备Id为空";
                    }
                    update(tr.children[2].id).catch(error=>{
                        console.log(error);
                    });
                });
            }else{
                console.log("当前设备Id未变");
            }
        });
        ld_list.appendChild(node.children[0]);
    });
    currentDevice.dataset.deviceId = ld_list.children[0].dataset.deviceId;
    currentDevice.innerText = ld_list.children[0].innerText;

    console.log(currentDevice.dataset.deviceId);
})();

function notify(msg, seconds){
    let tmp = document.querySelector(`#last_refresh_time`);
    tmp.innerText = msg;
    if(seconds && seconds > 0){
        setTimeout(()=>{
            tmp.innerText = "";
        }, seconds*1000)
    }
}

function updateAll() {
    let currentDevice = document.querySelector("#currentDevice");
    let deviceId = currentDevice.dataset.deviceId;
    if(!deviceId || !$.isNumeric(deviceId)){
        throw "非法的设备Id";
    }
    return new Promise((resolve, reject) => {
        $.ajax({
            url:`/api/data/last/${deviceId}`,
            type:"get",
            success:function(data){
                if(data && data.code === 0){
                    let list = document.querySelectorAll("table > tbody > tr");
                    list.forEach((tr, index) => {
                        let id = tr.children[2].id;
                        let key = tr.children[2].dataset.id;
                        let element = document.querySelector(`#${id}`);
                        element.innerText = data.json[key].value;
                        let elementBrother = document.querySelector(`#${id} + td`);
                        elementBrother.innerText = data.json[key].at;

                        if(id === "i3201_0_5551" || id === "i3201_1_5551"){
                            let cmd = document.querySelector(`#${id}_cmd`);
                            if(data.json.value === true){
                                element.innerText = "开";
                                cmd.innerText = "关闭";
                            }else{
                                element.innerText = "关";
                                cmd.innerText = "打开";
                            }
                        }
                    });
                    return resolve(data);
                }
                return reject();
            },
            error:function (jqXHR, textStatus, errorThrown) {
                return reject();
            }
        });
    });
}

function update(id){
    let currentDevice = document.querySelector("#currentDevice");
    let deviceId = currentDevice.dataset.deviceId;
    let tmp = id.substring(1);
    let array = tmp.split("_");
    let objId = array[0];
    let objInstId = array[1];
    let resId = array[2];
    let currentItem = document.querySelector(`#${id}`);
    let curretnItemBrother = document.querySelector(`#${id} + td`);
    return new Promise((resolve, reject) => {
        $.ajax({
            url:`/api/data/last/${deviceId}/${objId}/${objInstId}/${resId}`,
            type:"get",
            success:function(data){
                if(data && data.code === 0){
                    currentItem.innerText = data.json.value;
                    curretnItemBrother.innerText = data.json.at;
                    if(id === "i3201_0_5551" || id === "i3201_1_5551"){
                        let cmd = document.querySelector(`#${id}_cmd`);
                        if(data.json.value === true){
                            currentItem.innerText = "开";
                            cmd.innerText = "关闭";
                        }else{
                            currentItem.innerText = "关";
                            cmd.innerText = "打开";
                        }
                    }
                    return resolve();
                }else{
                    currentItem.innerText = "获取失败";
                    curretnItemBrother.innerText = "获取失败";
                    return reject();
                }
            },
            error:function (jqXHR, textStatus, errorThrown) {
                currentItem.innerText = "获取失败";
                curretnItemBrother.innerText = "获取失败";
                return reject();
            }
        });
    });
}

function execute(cmd, other){
    return new Promise((resolve, reject) => {
        $.ajax({
            url:`/api/execute`,
            type:"post",
            data:JSON.stringify({"args": `${cmd}`, "other": `${!other ? null : other}`}),
            contentType:"application/json;charset=utf-8",
            success:function(data){
                if(data && data.code === 0){
                    return resolve(data);
                }
                return reject();
            },
            error:function (jqXHR, textStatus, errorThrown) {
                return reject();
            }
        });
    });
}

let i3201_0_5551_cmd = document.querySelector(`#i3201_0_5551_cmd`);
i3201_0_5551_cmd.addEventListener("click", function(){
    let currentItem = document.querySelector(`#i3201_0_5551`);
    let curretnItemBrother = document.querySelector(`#i3201_0_5551 + td`);
    if(this.innerText === "打开"){//气象站
        execute("003").then(data => {
            currentItem.innerText = "开";
            this.innerText = "关闭";
        }, ()=>{
            notify(`打开气象站失败`, 2);
        });
    }else{
        execute("004").then(data => {
            currentItem.innerText = "关";
            this.innerText = "打开";
        }, ()=>{
            notify(`关闭气象站失败`, 2);
        });
    }
});

let i3201_1_5551_cmd = document.querySelector(`#i3201_1_5551_cmd`);
i3201_1_5551_cmd.addEventListener("click", function(){
    let currentItem = document.querySelector(`#i3201_1_5551`);
    let curretnItemBrother = document.querySelector(`#i3201_1_5551 + td`);
    if(this.innerText === "打开"){//屏幕
        execute("001").then(data => {
            currentItem.innerText = "开";
            this.innerText = "关闭";
        }, ()=>{
            notify(`打开屏幕失败`, 2);
        });
    }else{
        execute("002").then(data => {
            currentItem.innerText = "关";
            this.innerText = "打开";
        }, ()=>{
            notify(`关闭屏幕失败`, 2);
        });
    }
});

function addWaiting(id){
    let refreshId = `#${id}_refresh`;
    let selectId = `#${id}_select`;
    let waitingId = `#${id}_waiting`;
    let refresh = document.querySelector(refreshId);
    let select = document.querySelector(selectId);
    let waiting = document.querySelector(waitingId);
    refresh.addEventListener("click", event => {
        select.disabled = true;
        waiting.classList.remove("hide");
        update(id).then(data=>{
            notify("刷新成功", 2);
        }, ()=>{
            notify("刷新失败", 2);
        }).catch(error=>{
            console.log(error);
        }).finally(()=>{
            waiting.classList.add("hide");
            select.disabled = false;
        });
    });
}
let list = document.querySelectorAll("table > tbody > tr");
list.forEach((tr, key) => {
    addWaiting(tr.children[2].id);
});

let refreshAll = document.querySelector("#refreshAll");
refreshAll.addEventListener("click", event=>{
    event.target.classList.add("fa-spin");
    updateAll().then(data =>{
        notify("全部刷新成功", 2);
    },()=>{
        notify("全部刷新失败", 2);
    }).finally(()=>{
        event.target.classList.remove("fa-spin");
    });
});

updateAll();