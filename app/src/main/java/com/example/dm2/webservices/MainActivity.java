package com.example.dm2.webservices;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity {
    private Button btObetener;
    private String opeRes;

    private Spinner spinMedidas,spinMedidas2;
    private EditText txtDato;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btObetener = (Button)findViewById(R.id.btObtener);

        txtDato=(EditText)findViewById(R.id.txtDato);

        spinMedidas=(Spinner)findViewById(R.id.spinMedidas);
        spinMedidas2=(Spinner)findViewById(R.id.spinMedidas2);

        ArrayAdapter<CharSequence> adaptador = ArrayAdapter.createFromResource(this,
                R.array.medidas, android.R.layout.simple_spinner_item);
        adaptador.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);


        spinMedidas.setAdapter(adaptador);
        spinMedidas2.setAdapter(adaptador);

        btObetener.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String medida1 = spinMedidas.getSelectedItem().toString();
                String medida2 = spinMedidas2.getSelectedItem().toString();
                String dato = txtDato.getText().toString();

                AsyncPost task = new AsyncPost();
                task.execute(dato,medida1,medida2);
            }
        });
    }
    private class AsyncPost extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {
            try {
                HttpURLConnection conn;
                URL url = new URL("http://www.webservicex.net/length.asmx/ChangeLengthUnit");

                //Codificamos SOLO los valores de los parametros
                String param ="LengthValue="+ URLEncoder.encode(params[0],"UTF-8") +
                        "&fromLengthUnit="+URLEncoder.encode(params[1],"UTF-8")+
                        "&toLengthUnit="+URLEncoder.encode(params[2],"UTF-8");
                conn = (HttpURLConnection)url.openConnection();

                //establecer la salida a true, indicando que está
                //generando (cargando)datos POST
                conn.setDoOutput(true);

                //una vez establecida la salida a true, no es necesario establecer el
                //metodo de solicitud para publicar, pero lo hacemos de todos modos
                conn.setRequestMethod("POST");

                //La documentación de Android sugiere se establezca la longitud de los
                //datos que está enviando al servidor, PERO
                // NO especificar esta longitud en el encabezado usando
                //conn.setRequestProperty ("Content-Length", length);
                // usa esto en su lugar.
                conn.setFixedLengthStreamingMode(param.getBytes().length);
                conn.setRequestProperty("Content-Type","application/x-www-form-urlencoded");

                //enviar el POST
                PrintWriter out = new PrintWriter(conn.getOutputStream());
                out.print(param);
                out.close();

                //Construye la cadena para almacenar el texto de respuesta
                //desde el servidor
                String result ="";
                opeRes = "";

                //Comienza a escuchar el stream (flujo)
                Scanner inStream = new Scanner(conn.getInputStream());

                //Procesa el stream (flujo) y lo almacena en StringBuilder
                boolean a=false;
                while (inStream.hasNextLine()) {
                    result = inStream.nextLine();
                    if (result.indexOf("double") > 0) {
                        a = true;
                    }
                    if (a) {
                        if (result.indexOf("double") > 0)
                            opeRes = result.replace
                                    ("<double xmlns=\"http://www.webserviceX.NET/\">", "").replace("</double>", "");
                    }
                }
            }catch (MalformedURLException e){
                Log.e("A","excepcion MalformedURLException: "+e.getMessage());
            }catch (ProtocolException e){
                Log.e("A","excepcion ProtocolException: "+e.getMessage());
            } catch(IOException e){
                Log.e("A","excepcion IOException: "+e.getMessage());
            }catch (Exception e){
                Log.e("A","excepcion Exception: "+e.getMessage());
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void result) {
            Toast.makeText(MainActivity.this, "Resultado "+opeRes,Toast.LENGTH_LONG).show();
        }
    }
}
