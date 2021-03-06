package github.tornaco.xposedmoduletest.loader;

import android.content.Context;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import github.tornaco.xposedmoduletest.model.CommonPackageInfo;
import github.tornaco.xposedmoduletest.xposed.app.XAshmanManager;

/**
 * Created by guohao4 on 2017/10/18.
 * Email: Tornaco@163.com
 */

public interface PrivacyPackageLoader {

    @NonNull
    List<CommonPackageInfo> loadInstalled(boolean priv);

    class Impl implements PrivacyPackageLoader {

        public static PrivacyPackageLoader create(Context context) {
            return new Impl(context);
        }

        private Context context;

        private Impl(Context context) {
            this.context = context;
        }

        @NonNull
        @Override
        public List<CommonPackageInfo> loadInstalled(boolean priv) {

            List<CommonPackageInfo> out = new ArrayList<>();

            XAshmanManager xAshmanManager = XAshmanManager.get();
            if (!xAshmanManager.isServiceAvailable()) return out;

            String[] packages = xAshmanManager.getPrivacyList(priv);

            for (String pkg : packages) {
                CommonPackageInfo p = LoaderUtil.constructCommonPackageInfo(context, pkg);
                if (p != null && !p.isDisabled()) out.add(p);
            }

            LoaderUtil.commonSort(out);

            return out;
        }
    }
}
