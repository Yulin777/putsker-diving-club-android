package com.yulin.ivan.putsker;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CourseFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CourseFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CourseFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    ArrayList<Course> courses;
    ListView listView;
    Map<String, Object> m;
    Toolbar apptoolbar;
    static String title;

    private OnFragmentInteractionListener mListener;

    public CourseFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment CourseFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CourseFragment newInstance(String userName) {
        title = userName;
        CourseFragment fragment = new CourseFragment();
        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_course, container, false);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        initToolbar();
        courses = new ArrayList<Course>();

        String[] course_names = getResources().getStringArray(R.array.courses_names);
        TypedArray course_pics = getResources().obtainTypedArray(R.array.courses_pics);

        for (int i = 0; i < course_names.length; i++) {
            Course item = new Course(course_names[i],
                    course_pics.getResourceId(i, -1));
            courses.add(item);
        }

        listView = (ListView) getView().findViewById(R.id.coursesListFragment);
        @SuppressLint({"NewApi", "LocalSuppress"}) CustomCoursesAdapter adapter = new CustomCoursesAdapter(this.getContext(), courses);

        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), ClassActivity.class);
                Course c = (Course) listView.getItemAtPosition(position);
                String courseName = "";
                switch (c.getCourseName()) {
                    case "כוכב 1":
                        courseName = "star1";
                        break;
                    case "כוכב 2":
                        courseName = "star2";
                        break;
                    case "נייטרוקס":
                        courseName = "nitrox";
                        break;
                }
//                Object course = (Object) m.get(courseName);
                String nextTitle = c.getCourseName();
                intent.putExtra("title", nextTitle);
                intent.putExtra("isCourse", true);
                intent.putExtra("courseName", courseName);
//                intent.putExtra("classes", (Serializable) course);
                startActivity(intent);

            }
        });
    }


    private void initToolbar() {
        apptoolbar = getView().findViewById(R.id.apptoolbar);
        apptoolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
        apptoolbar.setTitle(title);
    }
}
