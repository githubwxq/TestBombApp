package com.example.administrator.testbombapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.administrator.testbombapp.bean.Childs;
import com.example.administrator.testbombapp.bean.Person;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobBatch;
import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BatchResult;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListListener;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        adddates();
       // querydates();
     addmoredates();

    }

    private void addmoredates() {
        List<BmobObject> persons=new ArrayList<BmobObject>() ;
        Person person1=new Person();
        person1.setName("wxq1");
        Person person2=new Person();
        person1.setName("wxq2");
        Person person3=new Person();
        person1.setName("wxq3");
        persons.add(person1);
        persons.add(person2);
        persons.add(person3);

        new BmobBatch().insertBatch(persons).doBatch(new QueryListListener<BatchResult>() {
            @Override
            public void done(List<BatchResult> list, BmobException e) {
                if (e == null) {
                    Toast.makeText(MainActivity.this, "成功添加多天数据:" + list.size(), Toast.LENGTH_SHORT).show();

                    for (int i = 0; i < list.size(); i++) {
                        BatchResult result = list.get(i);
                        Toast.makeText(MainActivity.this, result.getObjectId(), Toast.LENGTH_SHORT).show();
                    }


                }


            }
        });




    }

    private void querydates() {
        BmobQuery<Person> query = new BmobQuery<Person>();

        query.findObjects(new FindListener<Person>() {
            @Override
            public void done(List<Person> list, BmobException e) {
                if (e == null) {
                    Toast.makeText(MainActivity.this, "list.size():" + list.size(), Toast.LENGTH_SHORT).show();
                    Toast.makeText(MainActivity.this, "第一个课程数据" + list.get(1).getAllcourse().get(1), Toast.LENGTH_SHORT).show();
                    Toast.makeText(MainActivity.this, "第一个孩子兴趣数据" + list.get(2).getAllchilds().get(1).getHobby(), Toast.LENGTH_SHORT).show();

                    String objectId = list.get(1).getObjectId();
                    findPersonById(objectId);

                } else {
                    Toast.makeText(MainActivity.this, "bmob失败", Toast.LENGTH_SHORT).show();

                }
            }
        });


    }

    private void findPersonById(final String objectId) {
        BmobQuery<Person> bmodQuery = new BmobQuery<Person>();
        bmodQuery.getObject(objectId, new QueryListener<Person>() {
            @Override
            public void done(Person person, BmobException e) {
                if (e == null) {
                    Toast.makeText(MainActivity.this, "获取当前用户" + objectId + person.getAddress(), Toast.LENGTH_SHORT).show();


                    updatePerson(person.getObjectId());

                }


            }
        });


    }

    private void updatePerson(String objectId) {
        Person person = new Person();
        person.setAddress("更改了地址啦啦啦啦");
        // person.setObjectId(objectId);
        person.update(objectId, new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    Toast.makeText(MainActivity.this, "更新成功", Toast.LENGTH_SHORT).show();

                }
            }
        });
    }

    private void adddates() {
        Person p2 = new Person();
        p2.setName("lucky");
        p2.setAddress("北京海淀");
        List<String> allcourses = new ArrayList<>();
        allcourses.add("wxq");
        allcourses.add("liuyuan");
        p2.setAllcourse(allcourses);

        List<Childs> allchilds = new ArrayList<Childs>();
        allchilds.add(new Childs("大一", "踢篮球"));
        allchilds.add(new Childs("大二", "踢足球"));
        p2.setAllchilds(allchilds);
        p2.save(new SaveListener<String>() {
            @Override
            public void done(String objectId, BmobException e) {
                if (e == null) {
//
                    Toast.makeText(MainActivity.this, "上传成功" + objectId, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "上传失败", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
