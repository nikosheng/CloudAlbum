package hku.cs.cloudalbum;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static hku.cs.cloudalbum.CommunicationUtils.getResponseJson;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private ListView listView;
    private ArrayList<VideoItem> videoItems;
    private AlertDialog.Builder builder;
    private String filename;
    private List<Map<String, Object>> selections = new ArrayList<Map<String, Object>>();
    private List<Map<String, Object>> itemList = new ArrayList<Map<String, Object>>();

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
                synchronizeVideoList("jsfeng");
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        listView = (ListView) findViewById(R.id.list);
        synchronizeVideoList("jsfeng");

//        listView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE_MODAL);
//        listView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
//            @Override
//            public void onItemCheckedStateChanged(ActionMode actionMode, int position, long id, boolean checked) {
//                if (checked) {
//                    if (selections.isEmpty() ||
//                            (selections.size() > 0 && !selections.contains(itemList.get(position)))) {
//                        selections.add(itemList.get(position));
//                    }
//                } else {
//                    selections.remove(itemList.get(position));
//                }
//                actionMode.setTitle(selections.size() + " Selected");
//            }
//
//            @Override
//            public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
//                MenuInflater menuInflater = getMenuInflater();
//                menuInflater.inflate(R.menu.actionmode, menu);
//                return true;
//            }
//
//            @Override
//            public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
//                return false;
//            }
//
//            @Override
//            public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
//                boolean ret = false;
//
//                switch (menuItem.getItemId()) {
//                    case R.id.rename:
//                        if (selections.size() > 1) {
//                            break;
//                        } else {
//                            renameOperation(selections.get(0));
//                            ret = true;
//                        }
//                        break;
//                    case R.id.delete:
//                        deleteOperation(selections);
//                        ret = true;
//                        break;
//                }
//
//                return ret;
//            }
//
//            @Override
//            public void onDestroyActionMode(ActionMode actionMode) {
//                selections.clear();
//                actionMode = null;
//            }
//        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            String option;

            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int position, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Operation");
                final String[] options = {"RENAME", "DELETE"};
                builder.setSingleChoiceItems(options, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                option = "update";
                                break;
                            case 1:
                                option = "delete";
                                break;
                        }
                    }
                });

                builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (option) {
                            case "update":
                                renameOperation(itemList.get(position));
                                break;
                            case "delete":
                                deleteOperation(itemList.get(position));
                                break;
                        }
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        onVisibleBehindCanceled();
                    }
                });

                builder.show();
                return true;
            }
        });
    }

    public void synchronizeVideoList(final String csaccount) {
        final ProgressDialog pdialog = new ProgressDialog(this);

        pdialog.setCancelable(false);
        pdialog.setMessage("Updating ...");
        pdialog.show();

        final String url = CommunicationUtils.getRequestURL(csaccount, "synchronize");

        AsyncTask<String, Void, String> task = new AsyncTask<String, Void, String>() {
            boolean success;
            String jsonString;


            @Override
            protected String doInBackground(String... arg0) {
                // TODO Auto-generated method stub
                success = true;
                jsonString = getResponseJson(url);
                if (jsonString.equals(CommunicationUtils.RESULT_FAIL))
                    success = false;
                return null;
            }

            @Override
            protected void onPostExecute(String result) {
                if (success) {
                    videoItems = CommunicationUtils.parseJson(jsonString);
                    setData2ListView(videoItems);
                }
                pdialog.hide();
            }


        }.execute("");
    }

    private void setData2ListView(ArrayList<VideoItem> videoItems) {
        itemList.clear();
        for (int i = 0; i < videoItems.size(); i++) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("videoName", videoItems.get(i).getVideo_name());
            map.put("videoTime", videoItems.get(i).getUploadTimeStamp());
            map.put("videoId", String.valueOf(videoItems.get(i).getVideo_id()));
            map.put("videoUrl", videoItems.get(i).getVideo_url());

            itemList.add(map);
        }

        listView.setAdapter(new MyAdspter(this, itemList));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                HashMap<String, Object> item = (HashMap<String, Object>) listView.getAdapter().getItem(position);
//                String url = (String) item.get("videoUrl");
                Intent videoActivityIntent = new Intent(MainActivity.this, PlayerActivity.class);
                videoActivityIntent.putExtra("position", position);
                videoActivityIntent.putExtra("videoitems", (Serializable)itemList);
                startActivity(videoActivityIntent);
            }
        });
    }

    private void deleteOperation(Map<String, Object> item) {
        //Get the Layout Inflater
        final String videoId = (String) item.get("videoId");
        final String requestURL = CommunicationUtils.getRequestURL("jsfeng", "operation");

        AsyncTask<String, Void, String> asyncTask = new AsyncTask<String, Void, String>() {

            @Override
            protected String doInBackground(String... arg0) {
                // TODO Auto-generated method stub
                CommunicationUtils.httpUrlConnPost(requestURL, videoId, filename, "delete");
                return null;
            }

            @Override
            protected void onPostExecute(String result) {
                synchronizeVideoList("jsfeng");
            }
        }.execute("");
    }


    private void renameOperation(Map<String, Object> item) {
        //Get the Layout Inflater
        final View renameInfo = LayoutInflater.from(MainActivity.this).inflate(R.layout.rename_dialogue, null);
        final EditText renameText = (EditText) renameInfo.findViewById(R.id.video_rename);
        final String videoId = (String) item.get("videoId");
        final String requestURL = CommunicationUtils.getRequestURL("jsfeng", "operation");
        builder = new AlertDialog.Builder(MainActivity.this);
        builder.setView(renameInfo);

        builder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                filename = renameText.getText().toString();

                AsyncTask<String, Void, String> asyncTask = new AsyncTask<String, Void, String>() {

                    @Override
                    protected String doInBackground(String... arg0) {
                        // TODO Auto-generated method stub
                        CommunicationUtils.httpUrlConnPost(requestURL, videoId, filename, "update");
                        return null;
                    }

                    @Override
                    protected void onPostExecute(String result) {
                        synchronizeVideoList("jsfeng");
                    }
                }.execute("");
            }
        });

        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        onVisibleBehindCanceled();
                    }
                }
        );

        builder.setCancelable(false);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
            Intent launchactivity = new Intent(MainActivity.this, BrowserActivity.class);
            startActivity(launchactivity);
        }
//        else if (id == R.id.nav_gallery) {
//
//        } else if (id == R.id.nav_slideshow) {
//
//        } else if (id == R.id.nav_manage) {
//
//        } else if (id == R.id.nav_share) {
//
//        } else if (id == R.id.nav_send) {
//
//        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
