package com.example.administrator.term;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import android.database.Cursor;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;


public class MonthFragment extends Fragment implements View.OnClickListener {
    /**
     * 연/월 텍스트뷰
     */
    private TextView tvDate;
    /**
     * 그리드뷰 어댑터
     */
    private GridAdapter gridAdapter;
    /**
     * 일 저장 할 리스트
     */
    private ArrayList<String> dayList;
    /**
     * 그리드뷰
     */
    private GridView gridView;
    /**
     * 캘린더 변수
     */
    private Calendar mCal;

    private int Cmonth =0;

    Cursor cursor;
    MyDBHelper mDBHelper;

    public MonthFragment() {
        // Required empty public constructor
    }

    public static String chekedyear;
    public static String chekedmonth;
    public static String chekedday;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_month, container, false);
    }


    @Override
    public void onStart() {
        super.onStart();

        ImageButton btnMPrev = (ImageButton)getView().findViewById(R.id.btn_calendar_prevmonth);
        btnMPrev.setOnClickListener(this);
        ImageButton btnMNext = (ImageButton)getView().findViewById(R.id.btn_calendar_nextmonth);
        btnMNext.setOnClickListener(this);


        tvDate = (TextView)getView().findViewById(R.id.tv_date);
        gridView = (GridView)getView().findViewById(R.id.gridview);

        // 오늘에 날짜를 세팅 해준다.
        long now = System.currentTimeMillis();
        final Date date = new Date(now);

        //연,월,일을 따로 저장
        final SimpleDateFormat curYearFormat = new SimpleDateFormat("yyyy", Locale.KOREA);
        final SimpleDateFormat curMonthFormat = new SimpleDateFormat("MM", Locale.KOREA);
        final SimpleDateFormat curDayFormat = new SimpleDateFormat("dd", Locale.KOREA);



        //현재 날짜 텍스트뷰에 뿌려줌
        tvDate.setText(curYearFormat.format(date) + "/" + curMonthFormat.format(date));

        //gridview 요일 표시
        dayList = new ArrayList<String>();


        mCal = Calendar.getInstance();

        //이번달 1일 무슨요일인지 판단 mCal.set(Year,Month,Day)
        mCal.set(Integer.parseInt(curYearFormat.format(date)), Integer.parseInt(curMonthFormat.format(date)) - 1, 1);


        gridAdapter = new GridAdapter(getActivity().getApplicationContext(), dayList);
        setCalendarDate(mCal.get(Calendar.MONTH) + 1);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (!(position < 7 || dayList.get(position)=="")) {
                    chekedyear = "" + mCal.get(Calendar.YEAR);
                    chekedmonth = "" + (mCal.get(Calendar.MONTH) + 1);
                    chekedday = dayList.get(position);
                    String day= chekedyear+"/"+chekedmonth+"/"+chekedday;
                    //startActivity(new Intent(getActivity(), View_scheduleActivity.class));
                    Intent intent = new Intent(getActivity(), View_scheduleActivity.class);
                    intent.putExtra("ParamDate", day);

                    startActivity(intent);
                }
            }
        });
    }


    /**
     * 해당 월에 표시할 일 수 구함
     *
     * @param month
     */
    private void setCalendarDate(int month) {
        mCal.set(Calendar.MONTH, month - 1);
        int dayNum = mCal.get(Calendar.DAY_OF_WEEK);
        int lastmonthday = mCal.getActualMaximum(Calendar.DAY_OF_MONTH);
        dayList.clear();
        dayList.add("일");
        dayList.add("월");
        dayList.add("화");
        dayList.add("수");
        dayList.add("목");
        dayList.add("금");
        dayList.add("토");

        if(dayNum != 1) {
            for(int i =dayNum-2; i>=0; i--) {
                dayList.add("");
            }
        }
        for (int i = 0; i < mCal.getActualMaximum(Calendar.DAY_OF_MONTH); i++) {
            dayList.add("" + (i + 1));
        }
        gridView.setAdapter(gridAdapter);
    }

    @Override
    public void onClick(View view) {
        switch(view.getId())
        {
            case R.id.btn_calendar_prevmonth:
                mCal=getLastMonth(mCal);
                setCalendarDate(mCal.get(Calendar.MONTH) + 1);

                break;
            case R.id.btn_calendar_nextmonth:
                mCal=getNextMonth(mCal);
                setCalendarDate(mCal.get(Calendar.MONTH) + 1);
                break;

        }
    }
    private Calendar getLastMonth(Calendar calendar)
    {
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), 1);
        calendar.add(Calendar.MONTH, -1);
        tvDate.setText(mCal.get(Calendar.YEAR) + "/"
                + (mCal.get(Calendar.MONTH) + 1));
        return calendar;
    }
    private Calendar getNextMonth(Calendar calendar)
    {
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), 1);
        calendar.add(Calendar.MONTH, +1);
        tvDate.setText(mCal.get(Calendar.YEAR) + "/"
                + (mCal.get(Calendar.MONTH) + 1));
        return calendar;
    }
    /**
     * 그리드뷰 어댑터
     *
     */
    private class GridAdapter extends BaseAdapter {
        private final List<String> list;
        private final LayoutInflater inflater;
        /**
         * 생성자
         *
         * @param context
         * @param list
         */
        public GridAdapter(Context context, List<String> list) {
            this.list = list;
            this.inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return list.size();
        }
        @Override
        public String getItem(int position) {
            return list.get(position);
        }
        @Override
        public long getItemId(int position) {
            return position;
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.item_calendar_gridview, parent, false);
                holder = new ViewHolder();
                holder.tvItemGridView = (TextView)convertView.findViewById(R.id.tv_item_gridview);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder)convertView.getTag();
            }

            holder.tvItemGridView.setText("" + getItem(position));
            //해당 날짜 텍스트 컬러,배경 변경
            if (position <=6) {
                holder.tvItemGridView.setTextColor(getResources().getColor(R.color.color_000000));
            }
            mDBHelper = new MyDBHelper(getActivity(), "Today.db", null, 1);
            SQLiteDatabase db = mDBHelper.getWritableDatabase();
            cursor = db.rawQuery("SELECT * FROM today", null);
            if (cursor.getCount() != 0) {
                cursor.moveToFirst();
                for (int i = 1; i <= cursor.getCount(); i++) {
                String a = cursor.getString(3);
                String b = mCal.get(Calendar.YEAR) + "/" + (mCal.get(Calendar.MONTH) + 1) + "/" + getItem(position);
                    if (a.equals(b)) { //텍스트 컬러 변경
                        //holder.tvItemGridView.setTextColor(getResources().getColor(R.color.blue));
                        holder.tvItemGridView.setBackgroundResource(R.color.color_4);
                        holder.tvItemGridView.setWidth(150);
                    }
                    cursor.moveToNext();
                }

            }
            mDBHelper.close();
            return convertView;
        }
    }
    private class ViewHolder {
        TextView tvItemGridView;
    }

}