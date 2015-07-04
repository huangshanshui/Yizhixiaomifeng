package com.yizhixiaomifeng;
import com.yizhixiaomifeng.tools.ConnectWeb;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

public class Login extends Activity
{
    Handler handler;
    boolean result=true;
    private ImageView backImageView;
    private RadioGroup login_type;
    private String lt;  //��¼��¼����
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        backImageView=(ImageView)findViewById(R.id.back);
        backImageView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Login.this,MainActivity.class);
				startActivity(intent);
			}
		});
        
        
        /**
         * �����¼�¼�
         */
        
        TextView textView=(TextView)findViewById(R.id.register);
        final Button Login=(Button)findViewById(R.id.loginIn);
        final EditText username=(EditText)findViewById(R.id.username);
        final EditText password=(EditText)findViewById(R.id.password);
        login_type=(RadioGroup)findViewById(R.id.login_type);
        Login.setOnClickListener(new OnClickListener()
        {
            
            @Override
            public void onClick(View v)
            {
                final String user=username.getText().toString();
                final String pass=password.getText().toString();
                for(int i=0;i<login_type.getChildCount();i++)
				{
					RadioButton rb = (RadioButton)login_type.getChildAt(i);
					if(rb.isChecked())
					{
						lt = rb.getText().toString().trim();
						break;
					}
				}
                if(new ConnectWeb().isConnect(Login.this)==false)
                {
                    Toast.makeText(Login.this, "��������ʧ�ܣ���ȷ����������...",Toast.LENGTH_LONG).show();
                    return ;
                }
                if (user.equals("")||pass.equals("")||user==null||pass==null) {
                    Toast.makeText(Login.this, "��������Ϣ",Toast.LENGTH_LONG).show();
                    
                }
                else {
                	
                	
//                    new Thread(new Runnable() {
//                        
//                        public void run()
//                        {
//                            result=new ConnectWeb().checkUser(user, pass);
//                            Message m = handler.obtainMessage();
//                            handler.sendMessage(m); 
//                        }
//                    }).start();
                }
                
                handler = new Handler(){
                    public void handleMessage(Message msg) {
                        // ��ʾ��¼��                        
                        Login.setText("��¼��...");
                        Login.setEnabled(false);
                    
                        if(result){
                            
                            Intent intent=new Intent();
                            intent.putExtra("username", user);
                            intent.putExtra("password", pass);
                            intent.setClass(Login.this, MainActivity.class);     
                            startActivity(intent);
                            
                            //Intent intent=new Intent(Login.this, MainUI.class);  //��¼�ɹ�����ת
                            
                           // startActivity(intent);
                            Login.this.finish();
                            
                        }else{
                            Toast.makeText(getApplicationContext(), "��¼��Ϣ����!",Toast.LENGTH_LONG).show();
                            Login.setText("��¼");
                            Login.setEnabled(true);
                            
                        }
                        
                        super.handleMessage(msg);
                    }
                };
                
            }
        });
        
      /**
       *   Ϊע�����ְ�����
       */
        
        SpannableString sp = new SpannableString(textView.getText());
        final Intent intent = new Intent();
        intent.setClass(Login.this, Register.class);
        sp.setSpan(
                new IntentSpan(
                    new OnClickListener()
                    {  
                            public void onClick(View view)
                            {  
                                startActivity(intent);  
                            }  
    
                    }
                ),
                6,
                8,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
       );
       textView.setText(sp);  
       textView.setMovementMethod(LinkMovementMethod.getInstance());  
         
    }
    public class IntentSpan extends ClickableSpan {  
        
        
        private final OnClickListener listener;  

        public IntentSpan(View.OnClickListener listener) {  
            this.listener = listener;  
        }  

        @Override  
        public void onClick(View view) {  
            listener.onClick(view);  
        }  
    }  


}
