package gavs.first.com.home;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;

import java.util.List;

public class ViewPagerAdapter extends PagerAdapter {

    private Context context;
    private LayoutInflater layoutInflater;
    private List<SliderUtils> sliderImg;
    private ImageLoader imageLoader;


    public ViewPagerAdapter(List sliderImg,Context context) {
        this.sliderImg = sliderImg;
        this.context = context;
    }


    public int getCount() {
        return sliderImg.size();
    }


    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }


    public Object instantiateItem(ViewGroup container, final int position) {

        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.custom_layout, null);

        SliderUtils utils = sliderImg.get(position);

        ImageView imageView = (ImageView) view.findViewById(R.id.imageView);

        imageLoader = CustomVolleyRequest.getInstance(context).getImageLoader();
        imageLoader.get(utils.getSliderImageUrl(), ImageLoader.getImageListener(imageView, R.mipmap.ic_launcher, android.R.drawable.ic_dialog_alert));


        view.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                if(position == 0){
                    Toast.makeText(context, "Slide 1 Clicked", Toast.LENGTH_SHORT).show();
                } else if(position == 1){
                    Toast.makeText(context, "Slide 2 Clicked", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Slide 3 Clicked", Toast.LENGTH_SHORT).show();
                }

            }
        });

        ViewPager vp = (ViewPager) container;
        vp.addView(view, 0);
        return view;

    }


    public void destroyItem(ViewGroup container, int position, Object object) {

        ViewPager vp = (ViewPager) container;
        View view = (View) object;
        vp.removeView(view);

    }
}
