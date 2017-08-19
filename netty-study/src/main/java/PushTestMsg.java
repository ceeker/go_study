import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 测试消息发送工具
 * Created by vectorzhang on 2017/4/14.
 */
public class PushTestMsg {

    public static final String QQS = "409273291#8355227#277605373#970761001#451792374";
    /**
     * 测试消息请求模板
     */
    public static final String REQUEST_TEMPLATE = "{\n" +
            "    \"head\": {\n" +
            "        \"appname\": \"yaaf_mpqq_msgsendsvr\",\n" +
            "        \"appid\": 0,\n" +
            "        \"cmd\": 1,\n" +
            "        \"uin\": _qqNumber,\n" +
            "        \"proto\": 2,\n" +
            "        \"gid\": 10000,\n" +
            "        \"clientip\": 2887499964\n" +
            "    },\n" +
            "    \"body\": {\n" +
            "        \"content\": _msgContent,\n" +
            "        \"pc_uin\": 2892142631,\n" +
            "        \"msg_type\": 7,\n" +
            "        \"dir_flag\": 0,\n" +
            "        \"m_flag\": 0,\n" +
            "        \"auth_cgi\": \"cgi_qconn_public_inner:inner_api:/tnews_multi_message\",\n" +
            "        \"auth_key\": \"cgi\",\n" +
            "        \"auth_type\": 7,\n" +
            "        \"clt_uin\": _qqNumber,\n" +
            "        \"source_type\": 1,\n" +
            "        \"terminal\": 2,\n" +
            "        \"pkg_send_type\": 5\n" +
            "    }\n" +
            "}";
    private static final String FILE_FOLDER = "D:\\消息推送\\push_test";

    public static void main(String[] args) {
        HashMap<String, String> params = new HashMap<>();
        params.put("zk", "-zkservers=10.100.65.228:3181/zk/qconn/yaaf");
        params.put("exec", "qconncaserunner");
        params.put("password", "qq2013");
        String[] qqArr = QQS.split("#");

        List<String> allMaterial = getAllMaterial(FILE_FOLDER);
        if (null != allMaterial || !allMaterial.isEmpty()) {
            for (String msg : allMaterial) {
                for (int i = 0; i < qqArr.length; i++) {
                    String msgPack = getTestMsgPack(msg, qqArr[i]);
                    params.put("packet", msgPack);
                    try {
//                        String pushResult = HttpUtil.post("http://10.149.22.26/test-runner/action/v1", params);
                        String pushResult = HttpHelper.connect("http://10.149.22.26/test-runner/action/v1").data(params).post().html();
                        System.out.println(String.format("push test msg QQ=【%s】, result=【%s】", qqArr[i], pushResult));
                    } catch (Exception e) {
                        e.printStackTrace();
                        System.out.println("push test msg error,qqNumber=" + qqArr[i]);
                    }
                }
            }
        }

    }

    public static List<String> getAllMaterial(String materialPath) {
        List<String> msgList = null;
        File path = new File(materialPath);
        if (path.isDirectory()) {
            msgList = new ArrayList<>();
            File[] files = path.listFiles();
            for (int i = 0; i < files.length; i++) {
                if (!files[i].isDirectory()) {
                    try {
                        msgList.add(new String(Files.readAllBytes(Paths.get(files[i].getPath())), "utf-8"));
                    } catch (IOException e) {
                        System.out.println(e);
                    }
                }
            }
        }
        return msgList;
    }

    /**
     * 构造测试消息包
     *
     * @param msg
     * @param testQQNumber
     * @return
     */
    public static String getTestMsgPack(String msg, String testQQNumber) {
        return REQUEST_TEMPLATE.replace("_msgContent", msg).replace("_qqNumber", testQQNumber);
    }
}
