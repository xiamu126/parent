define(["jquery"], ($)=>{
    class Device{
        constructor(deviceId){
            this.deviceId = deviceId;
        }
        updateAll() {
            if(!this.deviceId || !$.isNumeric(this.deviceId)){
                throw "非法的设备Id";
            }
            return new Promise((resolve, reject) => {
                $.ajax({
                    url:`/api/data/last/${this.deviceId}`,
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
        update(id){
            let tmp = id.substring(1);
            let array = tmp.split("_");
            let objId = array[0];
            let objInstId = array[1];
            let resId = array[2];
            let currentItem = document.querySelector(`#${id}`);
            let curretnItemBrother = document.querySelector(`#${id} + td`);
            return new Promise((resolve, reject) => {
                $.ajax({
                    url:`/api/data/last/${this.deviceId}/${objId}/${objInstId}/${resId}`,
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
                            return reject();
                        }
                    },
                    error:function (jqXHR, textStatus, errorThrown) {
                        return reject();
                    }
                });
            });
        }
        execute(cmd, other){
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
    }
    return Device;
});