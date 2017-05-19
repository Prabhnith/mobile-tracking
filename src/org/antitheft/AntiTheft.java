package org.antitheft;

import java.util.Locale;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.*;

public class AntiTheft extends Activity
{
	private boolean english = false;
	private EditText editMagicWord;
	private EditText editNumber;
	private EditText editInterval;

	private Button btnOK;
	private Button btnCancel;

	private TextView labelMagicWord;
	private TextView labelNumber;
	private TextView labelInterval;

	public static String PREFS_NAME = "AntiTheftConfig";

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		editMagicWord = (EditText)AntiTheft.this.findViewById(R.id.magicword);
		editNumber = (EditText)AntiTheft.this.findViewById(R.id.number);
		editInterval = (EditText)AntiTheft.this.findViewById(R.id.interval);

		btnOK = (Button)findViewById(R.id.ok);
		btnCancel = (Button)findViewById(R.id.cancel);

		labelMagicWord = (TextView)AntiTheft.this.findViewById(R.id.labelMagicWord);
		labelNumber = (TextView)AntiTheft.this.findViewById(R.id.labelNumber);
		labelInterval = (TextView)AntiTheft.this.findViewById(R.id.labelInterval);

		//Get the stored preferences and set the EditTexts
		SharedPreferences settings = getSharedPreferences(AntiTheft.PREFS_NAME, 0);
		editMagicWord.setText(settings.getString("magicWord", ""));
		editNumber.setText(settings.getString("number", ""));
		editInterval.setText(Integer.toString(settings.getInt("interval", 20)/60000));

		if(!Locale.getDefault().getDisplayLanguage().toString().equals("fran�ais"))
		{
			english = true;

			labelMagicWord.setText("Enter the activation password:");
			labelNumber.setText("Enter the phone number on which will receive the location texts:");
			labelInterval.setText("Text sending interval (minutes):");
			btnCancel.setText("Cancel");
		}

		///////////////////
		//Buttons events//
		//////////////////
		btnOK.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{
				if(editMagicWord.getText().toString() != "" && editNumber.getText().toString() != "" && editInterval.getText().toString() != "")
				{
					SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
					SharedPreferences.Editor editor = settings.edit();
					editor.putString("magicWord",editMagicWord.getText().toString());
					editor.putString("number",editNumber.getText().toString());

					//To catch NumberFormatException if the user doesn't enter a integer for the interval
					try
					{
						//multiply by 1000 because the interval is in milliseconds
						editor.putInt("interval", Integer.parseInt(editInterval.getText().toString())*60000);

						//Commit the preferences edits
						editor.commit();

						Log.e("AntiTheft", "Configuration du mot de passe et du num�ro OK!");

						if(!english)
						{
							Toast.makeText(AntiTheft.this, "Configuration OK!\nMot de passe: "+editMagicWord.getText()+"\nNum�ro: "+editNumber.getText()+"\nIntervalle: "+editInterval.getText()+"min(s)", Toast.LENGTH_LONG).show();
						}
						else
						{
							Toast.makeText(AntiTheft.this, "Configuration OK!\nPassword: "+editMagicWord.getText()+"\nNumber: "+editNumber.getText()+"\nInterval: "+editInterval.getText()+"min(s)", Toast.LENGTH_LONG).show();
						}

						finish();
					}
					catch (NumberFormatException e)
					{
						Log.e("AntiTheft", "Erreur: "+e.getMessage());
						if(!english)
						{
							Toast.makeText(AntiTheft.this, "Veuillez remplir les champs correctement", Toast.LENGTH_LONG).show();
						}
						else
						{
							Toast.makeText(AntiTheft.this, "Please fill the fields correctly", Toast.LENGTH_LONG).show();
						}
					}
				}
				else
				{
					Toast.makeText(AntiTheft.this, "Veuillez remplir les champs correctement", Toast.LENGTH_LONG).show();
				}
			}
		});

		//Quit the application if the user clicks on "Cancel"
		btnCancel.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{
				finish();
			}
		});
	}

	@Override
	protected void onDestroy()
	{
		super.onDestroy();
	}
}