package com.example.closet;

import java.util.ArrayList;



import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends Activity {
	static final private int ADD_NEW_ITEM = Menu.FIRST;
	static final private int EDIT_ITEM = Menu.FIRST+1;
	static final private int REMOVE_ITEM = Menu.FIRST+2;

	private Resources res;
	private ArrayList<String> categoryItems;
	private ListView myListView;
	private ArrayAdapter<String> aa;

	public static final int SELECT_CODE_TOP=0;
	public static final int SELECT_CODE_BOTTOM=1;
	public static final int SELECT_CODE_SHOSE=2;

	@Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        res = getResources();

        myListView = (ListView)findViewById(R.id.closetListView);
        
        categoryItems = new ArrayList<String>();
        categoryItems.add(0, res.getString(R.string.category_top));
        categoryItems.add(1, res.getString(R.string.category_bottom));
        categoryItems.add(2, res.getString(R.string.category_shoes));
        
        aa = new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1, categoryItems);
        
    	Log.d("minbung", "1234567890");
        myListView.setAdapter(aa);
        
        myListView.setOnItemClickListener(new OnItemClickListener(){
        	public void onItemClick(AdapterView<?> parent, View v, int position, long id){
        		// 클릭된 아이템의 포지션을 이용해 어댑터뷰에서 아이템을 꺼내온다.
	            String blogUrl = (String) parent.getItemAtPosition(position);
        		Intent intent = new Intent(getBaseContext(), GridViewActivity.class);
        		
        		switch(position){
        		case SELECT_CODE_TOP:
        			intent.putExtra("cateString", res.getString(R.string.category_top));
        			intent.putExtra("cateNum", position);
            		startActivity(intent);
        			break;
        		case SELECT_CODE_BOTTOM:
        			intent.putExtra("cateString", res.getString(R.string.category_bottom));
        			intent.putExtra("cateNum", position);
            		startActivity(intent);
                	break;
        		case SELECT_CODE_SHOSE:
        			intent.putExtra("cateString", res.getString(R.string.category_shoes));
        			intent.putExtra("cateNum", position);
            		startActivity(intent);
        			break;
        			
        		}
        	}
        });
        
    }
    
    protected void onActivityResult(int reqCode, int resCode, Intent Data){
    	super.onActivityResult(reqCode, resCode, Data);
    	
    	if(reqCode==1){
//    		Toast.makeText(getBaseContext(),"onActivityResult call with code : "+resCode, Toast.LENGTH_SHORT).show();
    	}
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
	    super.onCreateOptionsMenu(menu);
	       
		//새 메뉴항목을 만들어 추가한다
  		MenuItem addItem = menu.add(0, ADD_NEW_ITEM, Menu.NONE, R.string.setting_add);
    	
   		//아이콘을 할당한다
   		addItem.setIcon(R.drawable.add_new_item);
   		
   		//이들 각각에 대한 닽축키를 할당한다.
        return true;

    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
    	super.onOptionsItemSelected(item);
		Intent intent = new Intent(getBaseContext(), AddActivity.class);
    	
    	switch(item.getItemId()){
    	case REMOVE_ITEM: {
    		return true;
    	}
    	case ADD_NEW_ITEM: {
    		startActivity(intent);
    		return true;
    	}
    	}
    	return false;
    }
  
}
