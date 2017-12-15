package github.tornaco.xposedmoduletest.ui.adapter;

import android.content.Context;
import android.support.annotation.ColorInt;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import dev.tornaco.vangogh.Vangogh;
import dev.tornaco.vangogh.display.CircleImageEffect;
import dev.tornaco.vangogh.display.appliers.FadeOutFadeInApplier;
import github.tornaco.android.common.Collections;
import github.tornaco.android.common.Consumer;
import github.tornaco.xposedmoduletest.R;
import github.tornaco.xposedmoduletest.bean.AutoStartPackage;
import github.tornaco.xposedmoduletest.loader.VangoghAppLoader;
import github.tornaco.xposedmoduletest.xposed.app.XAshmanManager;
import lombok.Getter;
import lombok.Setter;
import tornaco.lib.widget.CheckableImageView;

/**
 * Created by guohao4 on 2017/10/18.
 * Email: Tornaco@163.com
 */

public class StartAppListAdapter extends RecyclerView.Adapter<StartAppListAdapter.AppViewHolder>
        implements FastScrollRecyclerView.SectionedAdapter {

    private Context context;
    private VangoghAppLoader vangoghAppLoader;

    private CircleImageEffect circleImageEffect = new CircleImageEffect();

    @Getter
    private int selection = -1;

    @ColorInt
    @Setter
    private int highlightColor, normalColor;

    public StartAppListAdapter(Context context) {
        this.context = context;
        vangoghAppLoader = new VangoghAppLoader(context);


        this.highlightColor = ContextCompat.getColor(context, R.color.blue_grey);
        this.normalColor = ContextCompat.getColor(context, R.color.card);
    }

    final List<AutoStartPackage> autoStartPackages = new ArrayList<>();

    public void update(Collection<AutoStartPackage> src) {
        synchronized (autoStartPackages) {
            autoStartPackages.clear();
            autoStartPackages.addAll(src);
        }
        notifyDataSetChanged();
    }

    public void selectAll(final boolean selectAll) {
        Collections.consumeRemaining(getAutoStartPackages(), new Consumer<AutoStartPackage>() {
            @Override
            public void accept(AutoStartPackage startPackage) {
                startPackage.setAllow(selectAll);
            }
        });
        notifyDataSetChanged();
    }

    public void setSelection(int selection) {
        this.selection = selection;
        notifyDataSetChanged();
    }

    @Override
    public AppViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(getTemplateLayoutRes(), parent, false);
        return new AppViewHolder(view);
    }

    public List<AutoStartPackage> getAutoStartPackages() {
        return autoStartPackages;
    }

    @LayoutRes
    private int getTemplateLayoutRes() {
        return R.layout.app_list_item_1;
    }

    @Override
    public void onBindViewHolder(final AppViewHolder holder, int position) {
        final AutoStartPackage autoStartPackage = autoStartPackages.get(position);
        holder.getLineOneTextView().setText(autoStartPackage.getAppName());
        holder.getSystemAppIndicator().setVisibility(autoStartPackage.isSystemApp()
                ? View.VISIBLE : View.GONE);
        holder.getCheckableImageView().setChecked(false);
        Vangogh.with(context)
                .load(autoStartPackage.getPkgName())
                .skipMemoryCache(true)
                .usingLoader(vangoghAppLoader)
                .applier(new FadeOutFadeInApplier())
                .placeHolder(0)
                .fallback(R.mipmap.ic_launcher_round)
                .into(holder.getCheckableImageView());

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                removePkgAsync(autoStartPackage);
                return true;
            }
        });

        if (getSelection() >= 0 && position == selection) {
            holder.itemView.setBackgroundColor(highlightColor);
        } else {
            holder.itemView.setBackgroundColor(normalColor);
        }
    }

    protected void removePkgAsync(AutoStartPackage pkg) {
        XAshmanManager.get()
                .addOrRemoveStartBlockApps(new String[]{pkg.getPkgName()},
                        XAshmanManager.Op.REMOVE);
        onPackageRemoved(pkg.getPkgName());
    }

    protected void onPackageRemoved(String pkg) {

    }

    @Override
    public int getItemCount() {
        return autoStartPackages.size();
    }

    @NonNull
    @Override
    public String getSectionName(int position) {
        String appName = getAutoStartPackages().get(position).getAppName();
        if (appName == null
                || appName.length() < 1)
            appName = getAutoStartPackages().get(position).getPkgName();
        return String.valueOf(appName.charAt(0));
    }

    @Getter
    static class AppViewHolder extends RecyclerView.ViewHolder {
        private TextView lineOneTextView, systemAppIndicator;
        private CheckableImageView checkableImageView;

        AppViewHolder(View itemView) {
            super(itemView);
            lineOneTextView = itemView.findViewById(android.R.id.title);
            checkableImageView = itemView.findViewById(R.id.checkable_img_view);
            systemAppIndicator = itemView.findViewById(android.R.id.text1);
        }
    }
}
