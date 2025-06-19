package adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;

import androidx.recyclerview.widget.RecyclerView;

import com.example.howdoesthehumaneyeseethings.LessonDetailActivity;
import com.example.howdoesthehumaneyeseethings.R;
import model.LessonItem;

import java.util.List;

public class LessonAdapter extends RecyclerView.Adapter<LessonAdapter.LessonViewHolder> {

    private Context context;
    private List<LessonItem> lessonList;

    public LessonAdapter(Context context, List<LessonItem> lessonList) {
        this.context = context;
        this.lessonList = lessonList;
    }

    @Override
    public LessonViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_lesson, parent, false);
        return new LessonViewHolder(view);
    }

    @Override
    public void onBindViewHolder(LessonViewHolder holder, int position) {
        LessonItem item = lessonList.get(position);
        holder.titleTextView.setText(item.getTitle());
        holder.descriptionTextView.setText(item.getDescription());
        holder.imageView.setImageResource(item.getImageResId());
    }

    @Override
    public int getItemCount() {
        return lessonList.size();
    }

    public class LessonViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView, descriptionTextView;
        ImageView imageView;

        public LessonViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.lesson_title);
            descriptionTextView = itemView.findViewById(R.id.lesson_description);
            imageView = itemView.findViewById(R.id.lesson_image);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    LessonItem clickedLesson = lessonList.get(position);

                    Intent intent = new Intent(context, LessonDetailActivity.class);
                    intent.putExtra("title", clickedLesson.getTitle());
                    intent.putExtra("description", clickedLesson.getDescription());
                    intent.putExtra("imageResId", clickedLesson.getImageResId());
                    context.startActivity(intent);
                }
            });
        }
    }
}
