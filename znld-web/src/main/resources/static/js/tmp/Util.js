define(_=>{
    class Util {
        notify(msg, seconds, elementId="last_refresh_time") {
            if (seconds && seconds > 0) {
                let tmp = document.querySelector(`#${elementId}`);
                if(tmp.dataset.id && tmp.dataset.id>0){
                    clearTimeout(tmp.dataset.id);
                }
                tmp.innerText = msg;
                let id = setTimeout(() => {
                    tmp.innerText = "";
                }, seconds * 1000);
                tmp.dataset.id = id.toString();
            }
        }
    }
    return Util;
});