package vk;

import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.UserActor;
import com.vk.api.sdk.httpclient.HttpTransportClient;
import com.vk.api.sdk.objects.users.Fields;

import java.io.File;
import java.io.PrintWriter;

public class VKParser {

    // https://vk.com/apps?act=manage
    // IDAPP - id standalone приложения, надо будет создавать, а также указать openapi и "открыть" приложение в настройках
    // потом вставить в строку IDAPP, открыть в браузере
    // после в адресной строке скопировать access token и убрать другие параметры &

    private static final String access_token = "";//token

    public static void parseGroupMembers() throws Exception {
        var transportClient = new HttpTransportClient();
        var vk = new VkApiClient(transportClient);

        var id = 503725895l;// свой id вк
        var actor = new UserActor(id,access_token);

        var mem = vk.groups().getMembers(actor)
                .groupId("basicprogrammingrtf2023")
                .fields(Fields.BDATE,Fields.CITY,Fields.COUNTRY,Fields.SEX)
                .executeAsString();

        var file = new File("groupMembers.json");

        var pw = new PrintWriter(file);
        pw.println(mem);
        pw.close();
    }
}
