package App;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;

public class SendDataToServer extends Thread {

    byte[] content_bytes;

    Handler myHandler;

    public SendDataToServer(byte[] contents, Handler myHandler){
        this.content_bytes = contents;
        this.myHandler = myHandler;
    }

    public void run(){

        try{
            Log.e("FERWSJFW", "HELLO");
            Socket s = new Socket("192.168.1.2",8080);
            Log.e("FERERFW", "hello");
            ObjectOutputStream out =
                    new ObjectOutputStream(s.getOutputStream());
            ObjectInputStream in =
                    new ObjectInputStream(s.getInputStream());

            out.writeObject(content_bytes);
            out.flush();


            Route route = (Route) in.readObject();
            User user = (User) in.readObject();
            MasterData data = (MasterData) in.readObject();

            Message msg = new Message();
            Bundle bundle = new Bundle();
            bundle.putSerializable("route", route);
            bundle.putSerializable("user", user);
            bundle.putSerializable("data", data);
            msg.setData(bundle);
            s.close();

            myHandler.sendMessage(msg);

        }catch (IOException ioException) {
            ioException.printStackTrace();
        }catch(ClassNotFoundException e){
            throw new RuntimeException(e);
        }

    }


}
