package jpass;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import static jpass.DependencyFinder.getDependencies;

import org.apache.commons.codec.digest.DigestUtils;
import org.benf.cfr.reader.api.CfrDriver;

public class Router {
    
    public static List<Class> classList;    
    public static Map<Integer, Class> map;
    public static Map<Integer, String> keys;
    public static int[][] matrix;
    private static Map<Class, byte[]> decompiled = new HashMap<>();
    private static List<String> successful = new ArrayList<>();
    private static Map<Class, Integer> filesize = new ConcurrentHashMap<>();
    private static Thread T = new Thread();
    
    private static final Map<Integer, String> XOR = Map.ofEntries(
                    Map.entry(0, "mDi9L2odvcTauFoVBDcb9+dSc093Sxn9Ad2BQKLQ7DMiM5LUd8q+5gajnP/yFJ4nykluw4tCfv/IH1aWgb8CNw=="), 
                    Map.entry(1, "GycbFFaIUq6+OJNp31cZQvmTiW3mjBALuvLclairHwyAG0x4y+tGtbpn6d1+aij0naynBK03s13IsEO6j6BqrQ=="),
                    Map.entry(2, "7dYlZSDIB23fmmomcrNm5SCUZsG2R/gQr/bu2Mrkb/MVVw9J81/3ebpXCHuVFwiGr+E4Hi2oeCnGwzANcgY4yQ=="),
                    Map.entry(3, "2xDsP/QV9n3ETIdR88xslrdyYeVL1R+CFvxHmJcix6CAviWnQ84mAvJv87EKBPwq2VWA+N3N/Q+0rG0fo8Ardg=="),
                    Map.entry(4, "+k38jCKB8A0ZC8SiOZsnaAZx4z94ouKNgr9cmoeH6mI/7fhHvPlUJB/8KujwyQ6030DSmot2SGHQX4TpLzBNWQ=="),
                    Map.entry(5, "HZm3QLxvRdA1Emw5btO8FDZeiGzb3lL9Qdm1uFdBEEOnsH+phbvC9ll1PMPkqoa2C0C1PdjwL5eznFtD1TNt2Q=="),
                    Map.entry(6, "EhEiE1iuufXsLX3TnpSLAOiOik32dHf7ZgtAwrXV6k0jxP05hIEiXmN2qX2B5ohbG+QypYM57dACuRbKwTd3Tw=="),
                    Map.entry(7, "vPDunKBN9yYyuF+6rwLC99v6AGOgM00DSg8FoSGpX3Ro+JgxrQ8ZBjHwzGXyW7OX6sNzkhe5uJnwWM/0DANnTA=="),
                    Map.entry(8, "kZIZiFJR7m6hmRlAYlYXucF7EZy1vB4xk4F2szNMpqx4bnmTIawDDCJFqGKiPRFj8mkajr6kmzCNBE8nToO8jQ=="),
                    Map.entry(9, "xv5RSIsZc8LwvIUQONuldI9j0kTf+wNvkusGebLnav1+UuWjPF7y3Dn/7kCSscBnJU7A0Y5XPyGcWimUVtw7Jw=="),
                    Map.entry(10, "nM0rYl423GqF+Sz9YtpvPO7/1EdmHTxTbNAYEus0NdasIV9ijIcnwysXsY9xR8AfaUeP5W8ziotXbyiMFlpFcw=="),
                    Map.entry(11, "8goTICeKAdw7uzkex1ihKt1954awwv1mu9Qd6U5Nj5xR6/LZ7ro7HiDjh7QC55IE/Kwr8xKee+f7jH6KJ8ZCaA=="),
                    Map.entry(12, "KePyzl9wapY1086XiEYsTcjhIxjKgv40Ca3zM4gnEXmAYIa77NS9A08h4zUE0lJhDMxUTbO6qOug5HO1lKW3HA=="),
                    Map.entry(13, "g1gF+NSuij992TV8FNuGDlO9CgvCdZhdHefvTTGux6khTcz39e6xK+2h97kWueSfQe+ULMct50ZT43elsxxF0A=="),
                    Map.entry(14, "RxzjzLkJEOFMJeuE0JOtXRqV4dno9L0zEvhvuq8L0f+3x449IMt1th2PRwaVCTw4FF2z2S7bC2QCmIQH1Ev0Vg=="),
                    Map.entry(15, "l72uhOTSOj6Vppby/BZCq/jdu/a/npQGLWt28CE5BMH5KKJud+0UGLX/pTJOtw3nuf4b8CSzPWilRaRTBUgblg=="),
                    Map.entry(16, "DQdfcrG/+AYk8tu8b/Hnpeq4JW3jqnZaoJUjxYJ10j2tk6ogtEYkHl4GPzYio5yBE8rJRlhoQFc++mDs+Od09Q=="),
                    Map.entry(17, "1k+6tw2RyRYscWb8HHXbZapOunHka9W32qLjIPatox8uMjMjeoZmAMaNT0PyxIKELsQ0yz27IdNgUQdXi95QFw=="),
                    Map.entry(18, "LznhVFrZiXoqAfwj9uomUYrgVA3Fs7lyoVPxAf1cWFuPbR5bQfKpc4IiikEkcyLYeZR/h0q65nJuaw1OSQnG7g=="),
                    Map.entry(19, "/gIAWuPhyqRx/VoupYOGN4FSWlaynriHkUuRbN8j2v1TD5TlwC1X7GRwKZaFe55tMWMzMk+ffhDagEcb4DTN7A=="),
                    Map.entry(20, "iQH5+JWEIn683+fmCelPXlpE6RqTnEXkuDCs5bOMM3fSdpRUV+Pje7ySAfdSloKw//O1AuMQeyNxVnqKwPJYSg=="),
                    Map.entry(21, "L6InvitaC4VkIkYZZa1i42AAwVtIMR2J8tf8VMipoLdTJ+a1+SU7uLR1ne39jklV4VXZvQOEt31km6wzQWn7gA=="),
                    Map.entry(22, "3GN1SAxlSZI09OG8h0xqI5IjLAHHuSNcOQtD3PI4KrCyAdm+/ijBNGkrH/CUC9UO1OO5EswA+f/tbFqcDlAPzw=="),
                    Map.entry(23, "1qcZlgFlKiZUOee0/i8NNknx7VXxMqq884OI5ou8dcH3bxB4JmmyQFPfZdIzzZNkbYNLcxiXALqXJxtNlDSXwA=="),
                    Map.entry(24, "tOWosMUuXe6r1gOZ3UYmebAvP2S+AffrcTC//iBu7uPY949k4/EyyS71ZCj2W6nSXRCCk048UvBzLfRDGS3jHQ=="),
                    Map.entry(25, "2pt08ENctZBPHKqyALi62OMYFZvtlv4emkXHiZda9oLJ+QZ8k3XVZ8k81kUPwVzg/U45TBpxZh8EgDXqQTngbg=="),
                    Map.entry(26, "AsVdh+v9/oy9xyujvi+e05zIxqhb8XAYWZZxH4S2rd26o8aotsHan1wLMd2yTzxK2EZbzAC+WxF+8HdxhonlnA=="),
                    Map.entry(27, "FSR26dq418T88gaYpGZFCM4ZkC3c8AAYFobxAKlMHlFvYTx4IRuS39h0b8oKKybPCum4DTkjwdyqkP/QqjxQCw=="),
                    Map.entry(28, "Ya9eZjzaEHhCkH6yXUKCojeiGYUyYU3+8aUeleZc2ycYeD1cH4fG+hP0OmYiC8HYwKKiSpCgxLVLJ/9EDnP70Q=="),
                    Map.entry(29, "stLPRa+PnyDwqH63hT4Ow0G9q6iFrK3JjgTVOHZkEo9bw7o2zMIg7XryhB/lSbw7FIKLmO/Jq3B+KgzmjK72Mg=="),
                    Map.entry(30, "9WSxoZWmMkGfZS4MallelUPZ0cKdR1BeM8XsobjLIm9fcYXx/bDL0rBKlxvdcqp93vFeNRiYoG2upr0JuUwjeQ=="),
                    Map.entry(31, "Wlzl+dZI8ShDuSPFeZG9AVdkC1Gr4a0awoCQB9z4bcQ5TClZHXBUyUP1sn/0dMeIR6p1fRHliF/eH2Mc5Enxvg=="),
                    Map.entry(32, "rQ3CYb72xm/PiGeYEDCR6f+6DuL7iO+rC+O7Yp5ESNZQSGuHAjxmBNEbNgYWp+u+RROi2rsDFQjCeks5HsOzbA=="),
                    Map.entry(33, "57bBJIo9IN0cmfF4gIAY1su5FpSkbI/ksWLe7kopVzvseQwPRmmQHtpP4BK21ioyXeKL4lpgfMTLICvikH+Tmg=="));
    
    public static List<Class> getClassesForPackage(final String pckgname) throws ClassNotFoundException, IOException {
            ArrayList<File> directories = new ArrayList<>();
            String packageToPath = pckgname.replace('.', '\\');
            try {
                ClassLoader cld = Thread.currentThread().getContextClassLoader();
                if (cld == null) {
                    throw new ClassNotFoundException("Can't get class loader.");
                }
                Enumeration<URL> resources = cld.getResources(packageToPath);
                while (resources.hasMoreElements()) {
                    directories.add(new File(URLDecoder.decode(resources.nextElement().getPath(), "UTF-8")));
                }
            } catch (NullPointerException e) {
                throw new ClassNotFoundException(pckgname + " does not appear to be a valid package (Null pointer exception)");
            } catch (UnsupportedEncodingException e) {
                throw new ClassNotFoundException(pckgname + " does not appear to be a valid package (Unsupported encoding)");
            } catch (IOException e) {
                throw new ClassNotFoundException("IOException was thrown when trying to get all resources for " + pckgname);
            }

            ArrayList<Class> classes = new ArrayList<>();
            while (!directories.isEmpty()){
                File directoryFile  = directories.remove(0);
                if (directoryFile.exists()) {
                    File[] files = directoryFile.listFiles();
                    for (File file : files) {
                        if ((file.getName().endsWith(".class")) && (!file.getName().contains("$")) && (!file.getName().contains("Router")) && (!file.getName().contains("DependencyFinder"))) {
                            int index = directoryFile.getPath().indexOf(packageToPath);
                            String packagePrefix = directoryFile.getPath().substring(index).replace('\\', '.');                          
                            String className = packagePrefix + '.' + file.getName().substring(0, file.getName().length() - 6);
                            Class obj = Class.forName(className);
                            if(!Modifier.isAbstract(obj.getModifiers())) classes.add(obj);
                        } else if (file.isDirectory()) directories.add(new File(file.getPath()));                          
                    }
                } else throw new ClassNotFoundException(pckgname + " (" + directoryFile.getPath() + ") does not appear to be a valid package");
            }
            return classes;
        }
    
    public static Map<Integer, Class> createMap(final List<Class> classList) {
        
        HashMap<Integer, Class> result = new HashMap<>();
        
        for(int i = 0; i < classList.size(); i++) result.put(i, classList.get(i));
        
        return result;
    }
    
    public static Integer getKeyByValue(final Map<Integer, Class> map, final Class value) {
        return map.entrySet()
                .stream()
                .filter(entry -> Objects.equals(entry.getValue(), value))
                .map(Map.Entry::getKey)
                .findFirst()
                .get();
}
    
    public static int[][] createMatrix(final Map<Integer, Class> map) throws ClassNotFoundException, IOException {
        
        int[][] result = new int[map.size()][map.size()];
        
        for(Class cls : map.values()){
            Set<Class<?>> imp = getDependencies(cls);
            imp.stream().filter((dep) -> (dep.getCanonicalName() != null && dep.getCanonicalName().startsWith("jpass") && !dep.getCanonicalName().contains("Router") && !Modifier.isAbstract(dep.getModifiers()))).forEachOrdered((dep) -> {
                if(dep.getEnclosingClass() == null) result[getKeyByValue(map, cls)][getKeyByValue(map, dep)] = 1;
                else result[getKeyByValue(map, cls)][getKeyByValue(map, dep.getEnclosingClass())] = 1;
            });
            for(int i = 0; i < result.length; i++){
                for(int j = 0; j < result.length; j++){
                    if(result[i][j] == 1 && result[j][i] == 1) result[i][j] = 0;
                }
            }
        }
        return result;
    }
    
    public static List<Integer> topologicalSort(final int[][] mat){
        
        List<Integer> top = new ArrayList<>();
        List<Integer> remaining = new ArrayList<>();
        
        for (int i = 0; i < mat.length; i++) remaining.add(i);
        
        outer:
        while(!remaining.isEmpty()){
            for(Integer r : remaining){
                if(!hasDependency(mat, r, remaining)){
                    remaining.remove(r);
                    top.add(r);
                    continue outer;
                }
            }
        }
        return top;
    }
    
    private static boolean hasDependency(final int[][] mat, final Integer r, final List<Integer> elements){
        return elements.stream().anyMatch((i) -> (mat[r][i] == 1));
    }
    
    public static Map<Integer, String> createHash(final Map<Integer, Class> map, final List<Integer> topSort) {        
        
        Map<Integer, String> hashMap = new HashMap<>();
        
        topSort.forEach((i) -> {
            try {
                hashMap.put(i, generateKey(map.get(i), hashMap));
            } catch (NoSuchFieldException ex) {
                Logger.getLogger(Router.class.getName()).log(Level.SEVERE, ex.toString(), ex);
            } catch (IllegalArgumentException ex) {
                Logger.getLogger(Router.class.getName()).log(Level.SEVERE, ex.toString(), ex);
            } catch (IllegalAccessException ex) {
                Logger.getLogger(Router.class.getName()).log(Level.SEVERE, ex.toString(), ex);
            }
        });
        
        return hashMap;
    }
    
    private static String generateKey(final Class cls, final Map<Integer, String> hashMap) throws NoSuchFieldException, IllegalAccessException{

        byte[] prevhash = new byte[64];
        byte[] hash = new byte[64];
        byte[] nexthash = new byte[64];
        
        int index = getKeyByValue(Router.map, cls);
        
        for(int i = 0; i < Router.matrix.length; i++){
            if(Router.matrix[index][i] == 1){
                nexthash = Base64.getDecoder().decode(hashMap.get(i));
                for(int j = 0; j < 64; j++){
                    hash[j] = (byte) (prevhash[j] ^ nexthash[j]);
                }
                prevhash = hash;
            }
        }
        
        nexthash = Base64.getDecoder().decode(cls.getDeclaredField("ENCODING").get(new Object()).toString());
        
        for(int i = 0; i < 64; i++){
            hash[i] = (byte) (prevhash[i] ^ nexthash[i]);
        }
        
        return Base64.getEncoder().encodeToString(hash);     
    }
    
    public static Object callRouter(final Object obj, final Class cls, final String method, final Class<?>[] paramsType, final Object[] params) {
        
        try{
            
            if(successful.contains(new Exception().getStackTrace()[1].getClassName())){
                
                Method mtd = cls.getDeclaredMethod(method, paramsType);
                mtd.setAccessible(true);
                return (params == null) ? mtd.invoke(obj) : mtd.invoke(obj, params);
            }
            
            if(map == null) {
                
                Method mtd = cls.getDeclaredMethod(method, paramsType);
                mtd.setAccessible(true);
                return (params == null) ? mtd.invoke(obj) : mtd.invoke(obj, params);
                
            }
            
            else {

                String callerS = new Exception().getStackTrace()[1].getClassName();
                Class caller = (callerS.contains("$")) ? Class.forName(callerS.substring(0, callerS.indexOf("$"))) : Class.forName(callerS);
                String fullPath = caller.getProtectionDomain().getCodeSource().getLocation().getPath().replaceFirst("/", "") + caller.getPackageName().replace(".", "/") + "/" + caller.getSimpleName();
                
                byte[] digest = new byte[64];
                byte[] digestR = new byte[64];
                
                if(decompiled.containsKey(caller)){
                    
                    digest = Base64.getDecoder().decode(Router.keys.get(getKeyByValue(map, caller)));
                    digestR = decompiled.get(caller);
                    
                }
                
                else {

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    PrintStream ps = new PrintStream(baos);
                    PrintStream console = System.out;
                    System.setOut(ps);
                    CfrDriver driver = new CfrDriver.Builder().build();
                    driver.analyse(Collections.singletonList(fullPath));
                    System.out.flush();
                    System.setOut(console);
                    ps.close();
                    baos.close();

                    digest = Base64.getDecoder().decode(Router.keys.get(getKeyByValue(map, caller)));
                    digestR = DigestUtils.sha512(baos.toString());
                    decompiled.put(caller, digestR);
                    
                }
                    
                    byte[] xor = new byte[64];

                    for(int i = 0; i < 64; i++){
                        xor[i] = (byte) (digest[i] ^ digestR[i]);
                    }
                    
                    //System.out.println(caller + " - " + getKeyByValue(map, caller) + " - " + Base64.getEncoder().encodeToString(xor));

                    if(Base64.getEncoder().encodeToString(xor).equals(XOR.get(getKeyByValue(map, caller)))){
                        
                        successful.add(callerS);
                        filesize.put(caller, caller.getResource(caller.getSimpleName() + ".class").openStream().available());
                        if(!T.isAlive()) analyze();
                        Method mtd = cls.getDeclaredMethod(method, paramsType);
                        mtd.setAccessible(true);
                        return (params == null) ? mtd.invoke(obj) : mtd.invoke(obj, params);
                        
                    }
                    else {
                        
                        System.out.println("REPRESENTATIONS ARE DIFFERENT, STOPPING EXECUTION " + caller + " - " + getKeyByValue(map, caller) + " - " + Base64.getEncoder().encodeToString(xor));
                        System.exit(-1);
                        
                    }
                
            }
            
        } catch (IOException ex) {
            Logger.getLogger(Router.class.getName()).log(Level.SEVERE, ex.toString(), ex);
        } catch (NoSuchMethodException ex) {
            Logger.getLogger(Router.class.getName()).log(Level.SEVERE, ex.toString(), ex);
        } catch (SecurityException ex) {
            Logger.getLogger(Router.class.getName()).log(Level.SEVERE, ex.toString(), ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(Router.class.getName()).log(Level.SEVERE, ex.toString(), ex);
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(Router.class.getName()).log(Level.SEVERE, ex.toString(), ex);
        } catch (InvocationTargetException ex) {
            Logger.getLogger(Router.class.getName()).log(Level.SEVERE, ex.toString(), ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Router.class.getName()).log(Level.SEVERE, ex.toString(), ex);
        }
        return null;
    }
    
    private static void analyze(){
        T = new Thread(new Runnable() {
            @Override
            public void run() {
                while(true){
                    try {
                        for(Class c : filesize.keySet()){
                            if(!filesize.get(c).equals(c.getResource(c.getSimpleName() + ".class").openStream().available())){
                                System.out.println("FILE SIZE DIFFER FOR CLASS " + c.getSimpleName() + " ,STOPPING EXECUTION");
                                System.exit(-1);
                            }
                            Thread.sleep(5000);
                        }
                    } catch (InterruptedException ex) {
                        Logger.getLogger(Router.class.getName()).log(Level.SEVERE, ex.toString(), ex);
                    } catch (IOException ex) {
                        Logger.getLogger(Router.class.getName()).log(Level.SEVERE, ex.toString(), ex);
                    }
                }
            }            
        });
        T.start();
    }

}
