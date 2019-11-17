package com.sybd.znld.control;

import com.sybd.znld.util.MyDateTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class SunTimesUtil {
    private static Double start = 0.0;
    private static Double end = 0.0;
    private static Double sRA = 0.0;
    private static Double sdec = 0.0;
    private static Double sr = 0.0;
    private static Double lon = 0.0;

    public static void main(String[] args) {
        //今天
        var calendar  = Calendar.getInstance();
        var now = LocalDateTime.now();
        System.out.println(MyDateTime.toString(now, MyDateTime.FORMAT1));
        System.out.println( now.getDayOfMonth());
        for(var i = 1; i <= now.getDayOfMonth(); i++){

        }
        System.out.println(getSunTimeAtDate(MyDateTime.toDate(now),120.66124706341829,31.150935273203878 ));

    }

    public static HashMap<String,Object> getSunTimeAtDate(Date d, Double longitude, Double latitude) {
        long xcts = Days_since_2000_Jan_0(d);
        return GetSunTime(xcts,longitude, latitude);
    }

    public static HashMap<String,Object> GetSunTime(long day, Double longitude, Double latitude) {
        SunRiset(day, longitude, latitude, -35.0 / 60.0, 1,start,end);
        var sunrise = ToLocalTime(start);
        var sunset = ToLocalTime(end);
        HashMap<String,Object> hm = new HashMap<>(2);
        hm.put("sunRise",sunrise);
        hm.put("sunSet",sunset);
        return hm;
    }
    private static String ToLocalTime(Double utTime) {
        int hour = (int) (Math.floor(utTime));
        double temp = utTime - hour;
        hour += 8;
        temp = temp * 60;
        int minute = (int) (Math.floor(temp));
        String minuteStr = minute+" ";
        if(minute<10) {
            minuteStr = "0"+minute;
        }
        return hour+":"+minuteStr;
    }

    private static void Sunpos(Double d, Double lon, Double r) {
        Double M; //太阳的平均近点角，从太阳观察到的地球（=从地球看到太阳的）距近日点（近地点）的角度。
        Double w; //近日点的平均黄道经度。
        Double e; //地球椭圆公转轨道离心率。
        Double E; //太阳的偏近点角。计算公式见下面。
        Double x;
        Double y;
        Double v;  //真近点角，太阳在任意时刻的真实近点角。
        M = Revolution(356.0470 + 0.9856002585 * d);//自变量的组成：2000.0时刻太阳黄经为356.0470度,此后每天约推进一度（360度/365天
        w = 282.9404 + 4.70935E-5 * d;//近日点的平均黄经。
        e = 0.016709 - 1.151E-9 * d;//地球公转椭圆轨道离心率的时间演化。以上公式和黄赤交角公式一样，不必深究。
        E = M + e * Radge * Sind(M) * (1.0 + e * Cosd(M));
        x = Cosd(E) - e;
        y = Math.sqrt(1.0 - e * e) * Sind(E);
        setSr(Math.sqrt(x * x + y * y));
        v = Atan2d(y, x);
        lon = v + w;
        setLon(lon);
        if (lon >= 360.0) {
            lon -= 360.0;
            setLon(lon);
        }
    }

    private static void Sun_RA_dec(Double d,  Double RA,  Double dec, Double r) {
        Double obl_ecl, x, y, z;
        Sunpos(d, lon, r);
        //计算太阳的黄道坐标。
        x = sr * Cosd(lon);
        y = sr * Sind(lon);
        //计算太阳的直角坐标。
        obl_ecl = 23.4393 - 3.563E-7 * d;
        //黄赤交角，同前。
        z = y * Sind(obl_ecl);
        y = y * Cosd(obl_ecl);
        //把太阳的黄道坐标转换成赤道坐标（暂改用直角坐标）。
        setsRA(Atan2d(y, x));
        setSdec(Atan2d(z, Math.sqrt(x * x + y * y)));
        //最后转成赤道坐标。显然太阳的位置是由黄道坐标方便地直接确定的，但必须转换到赤
        //道坐标里才能结合地球的自转确定我们需要的白昼长度。
    }
    private static int SunRiset(long day, Double longitude, Double lat, Double altit, int upper_limb, Double trise, Double tset) {
        Double d;  /* Days since 2000 Jan 0.0 (negative before) */ //以历元2000.0起算的日数。
        Double sradius;/* Sun's apparent radius */ //太阳视半径，约16分（受日地距离、大气折射等诸多影响）
        Double t;/* Diurnal arc */ //周日弧，太阳一天在天上走过的弧长。
        Double tsouth; /* Time when Sun is at south */
        Double sidtime; /* Local sidereal time */ //当地恒星时，即地球的真实自转周期。比平均太阳日（日常时间）长3分56秒。
        int rc = 0; /* Return cde from function - usually 0 */
        /* Compute d of 12h local mean solar time */
        d = day/* Days_since_2000_Jan_0(date)*/ + 0.5 - longitude / 360.0;
        //计算观测地当日中午时刻对应2000.0起算的日数。
        /* Compute local sideral time of this moment */
        sidtime = Revolution(GMST0(d) + 180.0 + longitude);
        //计算同时刻的当地恒星时（以角度为单位）。以格林尼治为基准，用经度差校正。
        /* Compute Sun's RA + Decl at this moment */
        Sun_RA_dec(d, sRA,sdec,sr);
        //计算同时刻太阳赤经赤纬。
        /* Compute time when Sun is at south - in hours UT */
        tsouth = 12.0 - Rev180(sidtime - sRA) / 15.0;
        //计算太阳日的正午时刻，以世界时（格林尼治平太阳时）的小时计。
        /* Compute the Sun's apparent radius, degrees */
        sradius = 0.2666 / sr;
        //太阳视半径。0.2666是一天文单位处的太阳视半径（角度）。
        /* Do correction to upper limb, if necessary */
        if (upper_limb != 0)
            altit -= sradius;
        //如果要用上边缘，就要扣除一个视半径。
        /* Compute the diurnal arc that the Sun traverses to reach */
        //计算周日弧。直接利用球面三角公式。如果碰到极昼极夜问题，同前处理。
        /* the specified altitide altit: */
        var cost = (Sind(altit) - Sind(lat) * Sind(sdec)) / (Cosd(lat) * Cosd(sdec));
        if (cost >= 1.0) {
            rc = -1;
            t = 0.0;
        } else {
            if (cost <= -1.0) {
                rc = +1;
                t = 12.0;      /* Sun always above altit */
            } else t = Acosd(cost) / 15.0;   /* The diurnal arc, hours */
        }
        /* Store rise and set times - in hours UT */
        setStart(tsouth - t);
        setEnd(tsouth + t);
        return rc;
    }
    private static long Days_since_2000_Jan_0(Date date) {
        var d2000 = "2000-01-01";
        var sdf = new SimpleDateFormat("yyyy-MM-dd");
        long ll = 0L;
        try {
            ll = date.getTime()-sdf.parse(d2000).getTime();
        } catch (ParseException ignored) { }
        return ll/1000/60/60/24;
    }

    private static Double Revolution(Double x) {
        return (x - 360.0 * Math.floor(x * Inv360));
    }

    private static Double Rev180(Double x) {
        return (x - 360.0 * Math.floor(x * Inv360 + 0.5));
    }

    private static Double GMST0(Double d) {
        return Revolution((180.0 + 356.0470 + 282.9404) + (0.9856002585 + 4.70935E-5) * d);
    }


    private static Double Inv360 = 1.0 / 360.0;
    private static Double Sind(Double x) {
        return Math.sin(x * Degrad);
    }

    private static Double Cosd(Double x) {
        return Math.cos(x * Degrad);
    }

    private static Double Acosd(Double x) {
        return Radge * Math.acos(x);
    }

    private static Double Atan2d(Double y, Double x) {
        return Radge * Math.atan2(y, x);

    }

    private static Double Radge = 180.0 / Math.PI;
    private static Double Degrad = Math.PI / 180.0;

    public static Double getStart() {
        return start;
    }

    public static void setStart(Double start) {
        SunTimesUtil.start = start;
    }

    public static Double getsRA() {
        return sRA;
    }

    public static void setsRA(Double sRA) {
        SunTimesUtil.sRA = sRA;
    }

    public static Double getSdec() {
        return sdec;
    }

    public static void setSdec(Double sdec) {
        SunTimesUtil.sdec = sdec;
    }

    public static Double getSr() {
        return sr;
    }

    public static void setSr(Double sr) {
        SunTimesUtil.sr = sr;
    }

    public static Double getLon() {
        return lon;
    }

    public static void setLon(Double lon) {
        SunTimesUtil.lon = lon;
    }

    public static Double getEnd() {
        return end;
    }

    public static void setEnd(Double end) {
        SunTimesUtil.end = end;
    }
}
