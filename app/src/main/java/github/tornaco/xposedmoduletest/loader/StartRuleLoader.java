package github.tornaco.xposedmoduletest.loader;

import android.content.Context;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import github.tornaco.xposedmoduletest.BuildConfig;
import github.tornaco.xposedmoduletest.model.CommonPackageInfo;
import github.tornaco.xposedmoduletest.xposed.app.XAshmanManager;

/**
 * Created by guohao4 on 2017/10/18.
 * Email: Tornaco@163.com
 */

public interface StartRuleLoader {

    @NonNull
    List<CommonPackageInfo> loadInstalled();

    class Impl implements StartRuleLoader {

        public static StartRuleLoader create(Context context) {
            return new Impl(context);
        }

        private Context context;

        private Impl(Context context) {
            this.context = context;
        }

        @NonNull
        @Override
        public List<CommonPackageInfo> loadInstalled() {

            List<CommonPackageInfo> out = new ArrayList<>();

            XAshmanManager xAshmanManager = XAshmanManager.get();
            if (!xAshmanManager.isServiceAvailable()) return out;

            String[] rules = xAshmanManager.getStartRules();

            for (String pkg : rules) {
                CommonPackageInfo p = new CommonPackageInfo();
                p.setPkgName(BuildConfig.APPLICATION_ID);
                p.setAppName(pkg);
                out.add(p);
            }

            java.util.Collections.sort(out, new PinyinComparator());

            return out;
        }
    }

    class PinyinComparator implements Comparator<CommonPackageInfo> {
        public int compare(CommonPackageInfo o1, CommonPackageInfo o2) {
            return new github.tornaco.xposedmoduletest.util.PinyinComparator().compare(o1.getAppName(), o2.getAppName());
        }
    }
}
