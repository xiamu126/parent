package com.sybd.znld.service.rbac.dto;

import java.io.Serializable;
import java.util.List;

public class RbacInfo implements Serializable {
    public Rbac rbac;
    public static class Rbac implements Serializable{
        public Html html;
        public Server server;
        public static class Html implements Serializable{
            public List<Exclude> excludes;
            public List<Include> includes;
            public static class Exclude implements Serializable{
                public String path;
                public List<String> selectors;
            }
            public static class Include implements Serializable{
                public String path;
                public List<String> selectors;
            }
        }
        public static class Server implements Serializable{
            public List<Include> includes;
            public List<Exclude> excludes;
            public static class Include implements Serializable{
                public List<String> methods;
                public String path;
            }
            public static class Exclude implements Serializable{
                public List<String> methods;
                public String path;
            }
        }
    }
}
