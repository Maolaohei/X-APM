package github.tornaco.xposedmoduletest.ui.tiles;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import dev.nick.tiles.tile.QuickTile;
import dev.nick.tiles.tile.QuickTileView;
import github.tornaco.xposedmoduletest.R;
import github.tornaco.xposedmoduletest.ui.activity.nf.NetworkRestrictActivity;

/**
 * Created by guohao4 on 2017/11/10.
 * Email: Tornaco@163.com
 */

public class NFManager extends QuickTile {

    public NFManager(final Context context) {
        super(context);
        this.titleRes = R.string.title_nf;
        this.summaryRes = R.string.summary_exp;
        this.iconRes = R.drawable.ic_data_usage_black_24dp;
        this.tileView = new QuickTileView(context, this) {
            @Override
            public void onClick(View v) {
                super.onClick(v);
                context.startActivity(new Intent(context, NetworkRestrictActivity.class));
            }
        };
    }
}
