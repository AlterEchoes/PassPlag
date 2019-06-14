package jpass;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import static jpass.Router.getKeyByValue;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.benf.cfr.reader.api.CfrDriver;

public class RouterUtils {
    
    private static void modifyLine(final String className, final String pkg, final int lineNumber) throws IOException, ClassNotFoundException{
        
        Router.classList = Router.getClassesForPackage("jpass");

        String dir = System.getProperty("user.dir") + "\\src\\" + pkg.replace('.', '\\') + "\\" + className + ".java";
        
        File f = new File(dir);
        
        String encoding = System.getProperty("file.encoding");
        
        String line = FileUtils.readLines(f, encoding).get(lineNumber - 1);
        
        List<String> fileContent = new ArrayList<>(FileUtils.readLines(f, encoding));
        
        for(int i = 0; i < fileContent.size(); i++){
            if(fileContent.get(i).equals(line)){
                
                String leftSide = "";                            
                String declaringClass = "";                                
                String paramTypes = "";
                
                if(line.contains("=")){
                    leftSide = line.substring(0, line.indexOf("=") + 2);
                    line = line.substring(leftSide.length(), line.length());
                }
                else{
                    leftSide = line.substring(0, line.length() - line.trim().length());
                    line = line.trim();
                }
                
                String obj = (line.contains(".")) ? line.substring(0, line.indexOf(".")) : null;
                if(obj != null && Character.isUpperCase(obj.codePointAt(0))){
                    declaringClass = obj;
                    obj = null;
                }
                String methodName = (line.contains(".")) ? line.substring(line.indexOf(".") + 1, line.indexOf("(")) : line.substring(0, line.indexOf("("));
                String params = line.substring(line.indexOf("(") + 1, line.indexOf(")"));
                if(params.length() == 0) params = null;
                
                if(declaringClass.equals("")){
                    for(Class c : Router.classList){
                        Method[] mtds = c.getDeclaredMethods();
                        for(Method m : mtds){
                            if(m.getName().equals(methodName)) {
                                declaringClass = c.getSimpleName();
                                if(m.getParameters().length == 0) break;
                                for(Parameter p : m.getParameters()){
                                    paramTypes += p.getType().getSimpleName() + ".class, ";
                                }
                                paramTypes = paramTypes.substring(0, paramTypes.length() - 2);
                                break;
                            }
                        }
                    }
                }
                else {
                    for(Class c : Router.classList){
                        if(c.getSimpleName().equals(declaringClass)){
                            Method[] mtds = c.getDeclaredMethods();
                            for(Method m : mtds){
                                if(m.getName().equals(methodName)) {
                                    if(m.getParameters().length == 0) break;
                                    for(Parameter p : m.getParameters()){
                                        paramTypes += p.getType().getSimpleName() + ".class, ";
                                    }
                                    paramTypes = paramTypes.substring(0, paramTypes.length() - 2);
                                    break;
                                }
                            }
                        }
                    }
                }
                String replacement = leftSide + "Router.callRouter(" + obj + ", " + declaringClass + ".class, " +
                                     "\"" + methodName + "\", new Class<?>[]{" + paramTypes + "}, new Object[]{" + params + "});";
                fileContent.set(i, replacement);
                break;
            }
        }        
        FileUtils.writeLines(f, fileContent);        
    }
    
    private static void setEncoding(final String className, final String pkg) throws IOException{
        
        String dir = System.getProperty("user.dir") + "\\src\\" + pkg.replace('.', '\\') + "\\" + className + ".java";
        
        File f = new File(dir);
        
        String encoding = System.getProperty("file.encoding");
        
        List<String> fileContent = new ArrayList<>(FileUtils.readLines(f, encoding));
        
        String repr = "";
        int encodingLine = -1;
        
        for(int i = 0; i < fileContent.size(); i++){
            if(fileContent.get(i).contains("ENCODING")) {
                encodingLine = i;
                fileContent.set(i, "public static final String ENCODING = \"\";");
            }
            repr += fileContent.get(i) + "\n";
        }
        
        byte[] digest = DigestUtils.sha512(repr);
        String encode = Base64.getEncoder().encodeToString(digest);
        fileContent.set(encodingLine, "public static final String ENCODING = \"" + encode + "\";");
        
        FileUtils.writeLines(f, fileContent);
    }
    
    private static void getXOR(final String className, final String pkg) throws ClassNotFoundException, IOException{
        
        Router.classList = Router.getClassesForPackage("jpass"); 
        Router.map = Router.createMap(Router.classList);
        Router.matrix = Router.createMatrix(Router.map);      
        List<Integer> lst = Router.topologicalSort(Router.matrix);       
        Router.keys = Router.createHash(Router.map, lst);
        
        Class cls = Class.forName(pkg + "." + className);
        String path = cls.getProtectionDomain().getCodeSource().getLocation().getPath().replaceFirst("/", "") + cls.getPackageName().replace(".", "/") + "/" + cls.getSimpleName();
        
        byte[] digest = new byte[64];
        byte[] digestR = new byte[64];
        
        digest = Base64.getDecoder().decode(Router.keys.get(getKeyByValue(Router.map, cls)));
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        PrintStream console = System.out;
        System.setOut(ps);
        CfrDriver driver = new CfrDriver.Builder().build();
        driver.analyse(Collections.singletonList(path));
        System.out.flush();
        System.setOut(console);
        ps.close();
        baos.close();
        digestR = DigestUtils.sha512(baos.toString());
        
        byte[] xor = new byte[64];

        for(int i = 0; i < 64; i++){
            xor[i] = (byte) (digest[i] ^ digestR[i]);
        }
        System.out.println(Base64.getEncoder().encodeToString(xor));
    }
}
