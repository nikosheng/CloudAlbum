package hku.cs.cloudalbum;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;
import java.util.Map;


public class MyAdspter extends BaseAdapter {
    private List<Map<String, Object>> data;
    private LayoutInflater layoutInflater;
    private Context context;
    public MyAdspter(Context context, List<Map<String, Object>> data){
        this.context=context;
        this.data=data;
        this.layoutInflater= LayoutInflater.from(context);
    }
    /**
     * 组件集合，对应list.xml中的控件
     * @author Administrator
     */

    @Override
    public int getCount() {
        return data.size();
    }
    /**
     * 获得某一位置的数据
     */
    @Override
    public Object getItem(int position) {
        return data.get(position);
    }
    /**
     * 获得唯一标识
     */
    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ListViewItem listViewItem = null;
        if(convertView==null){
            listViewItem=new ListViewItem();
            //获得组件，实例化组件
            convertView=layoutInflater.inflate(R.layout.list, null);
            listViewItem.setVideoName((TextView)convertView.findViewById(R.id.videoName));
            listViewItem.setVideoTime((TextView)convertView.findViewById(R.id.videoTime));
            listViewItem.setVideoId((TextView)convertView.findViewById(R.id.videoId));
            listViewItem.setVideoUrl((TextView)convertView.findViewById(R.id.videoUrl));
            convertView.setTag(listViewItem);
        }else{
            listViewItem=(ListViewItem)convertView.getTag();
        }
        //绑定数据
        listViewItem.getVideoName().setText((String)data.get(position).get("videoName"));
        listViewItem.getVideoTime().setText((String)data.get(position).get("videoTime"));
        listViewItem.getVideoId().setText((String)data.get(position).get("videoId"));
        listViewItem.getVideoUrl().setText((String)data.get(position).get("videoUrl"));
        return convertView;
    }

}
