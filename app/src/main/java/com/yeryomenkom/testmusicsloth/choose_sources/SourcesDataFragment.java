package com.yeryomenkom.testmusicsloth.choose_sources;

import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.google.gson.Gson;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;

import java.util.ArrayList;
import java.util.Collections;

import ua.yeryomenkom.musicsloth.R;
import ua.yeryomenkom.musicsloth.Result;
import ua.yeryomenkom.musicsloth.Utils;
import ua.yeryomenkom.musicsloth.VKRequestManager;
import ua.yeryomenkom.musicsloth.VkOfflinePlayerApplication;
import ua.yeryomenkom.musicsloth.sync.SPlaylist;
import ua.yeryomenkom.musicsloth.vk_essences.ResponseFromAudioGetAlbumsRequest;
import ua.yeryomenkom.musicsloth.vk_essences.ResponseFromFriendsGetRequest;
import ua.yeryomenkom.musicsloth.vk_essences.ResponseFromGroupsGetRequest;
import ua.yeryomenkom.musicsloth.vk_essences.ResponseFromSubscriptionsGetRequest;
import ua.yeryomenkom.musicsloth.vk_essences.VKAudioAlbumItem;
import ua.yeryomenkom.musicsloth.vk_essences.VkGroupItem;
import ua.yeryomenkom.musicsloth.vk_essences.VkMusicGenres;
import ua.yeryomenkom.musicsloth.vk_essences.VkUserItem;

/**
 * Created by Misha on 06.05.2015.
 */
public class SourcesDataFragment extends Fragment {
    VkAudioAlbumsTreeFragment vkAudioAlbumsTreeFragment;
    SelectedVkAudioAlbumsFragment selectedVkAudioAlbumsFragment;

    private final int REQUEST_TIMEOUT = 3000;

    SPlaylist currentSPlaylist;

    private ArrayList<VKAudioAlbumItem> popularAlbums;

    ArrayList<VkSource> sourcesFriends, sourcesGroups, sourcesSubscriptions;
    ArrayList<VKAudioAlbumItem> lastAlbumsList;

    private long lastRequestCodeForGetAlbums;

    private SourcesAvailableListener sourcesAvailableListener;

    private boolean isGroupDownloadingStarted, isFriendsDownloadingStarted, isSubscriptionsDownloadingStarted;
    private boolean lastRequestCodeForGetAlbumsWasZero;

    public void setCurrentSPlaylist(@Nullable SPlaylist currentSPlaylist) {
        if (currentSPlaylist == null) {
            currentSPlaylist = new SPlaylist();
            currentSPlaylist.date = Utils.getCurrentDate();
        }
        this.currentSPlaylist = currentSPlaylist;
        selectedVkAudioAlbumsFragment.setSelectedAudioAlbums(currentSPlaylist.albums);
    }

    public void albumUnSelectedFromTreeFragment() {
        selectedVkAudioAlbumsFragment.albumsUnSelectedFromTreeFragment();
    }

    public void albumSelectedFromTreeFragment() {
        selectedVkAudioAlbumsFragment.albumSelectedFromTreeFragment();
    }

    public void albumUnSelectedFromSelectedFragment(String uniqAlbumString) {
        vkAudioAlbumsTreeFragment.albumUnSelectedFromSelectedFragment(uniqAlbumString);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        lastRequestCodeForGetAlbumsWasZero =
                isFriendsDownloadingStarted =
                        isGroupDownloadingStarted =
                                isSubscriptionsDownloadingStarted = false;

        initializePopularAlbums();
    }

    private void initializePopularAlbums() {
        popularAlbums = new ArrayList<>(21);
        //здесь id альбома будет служить id жанра
        popularAlbums.add(new VKAudioAlbumItem(VkMusicGenres.All,0,"All genres"));
        popularAlbums.add(new VKAudioAlbumItem(VkMusicGenres.Rock,0,"Rock"));
        popularAlbums.add(new VKAudioAlbumItem(VkMusicGenres.Pop,0,"Pop"));
        popularAlbums.add(new VKAudioAlbumItem(VkMusicGenres.Rap_Hip_Hop,0,"Rap & Hip-Hop"));
        popularAlbums.add(new VKAudioAlbumItem(VkMusicGenres.Easy_Listening,0,"Easy Listening"));
        popularAlbums.add(new VKAudioAlbumItem(VkMusicGenres.Dance_House,0,"Dance & House"));
        popularAlbums.add(new VKAudioAlbumItem(VkMusicGenres.Instrumental,0,"Instrumental"));
        popularAlbums.add(new VKAudioAlbumItem(VkMusicGenres.Metal,0,"Metal"));
        popularAlbums.add(new VKAudioAlbumItem(VkMusicGenres.Alternative,0,"Alternative"));
        popularAlbums.add(new VKAudioAlbumItem(VkMusicGenres.Dubstep,0,"Dubstep"));
        popularAlbums.add(new VKAudioAlbumItem(VkMusicGenres.Jazz_Blues,0,"Jazz & Blues"));
        popularAlbums.add(new VKAudioAlbumItem(VkMusicGenres.Drum_Bass,0,"Drum & Bass"));
        popularAlbums.add(new VKAudioAlbumItem(VkMusicGenres.Trance,0,"Trance"));
        popularAlbums.add(new VKAudioAlbumItem(VkMusicGenres.Ethnic,0,"Ethnic"));
        popularAlbums.add(new VKAudioAlbumItem(VkMusicGenres.Acoustic_Vocal,0,"Acoustic & Vocal"));
        popularAlbums.add(new VKAudioAlbumItem(VkMusicGenres.Reggae,0,"Reggae"));
        popularAlbums.add(new VKAudioAlbumItem(VkMusicGenres.Classical,0,"Classical"));
        popularAlbums.add(new VKAudioAlbumItem(VkMusicGenres.Indie_Pop,0,"Indie Pop"));
        popularAlbums.add(new VKAudioAlbumItem(VkMusicGenres.Electropop_Disco,0,"Electropop & Disco"));
    }


    void getGroups(int requestCode) {
        if(sourcesGroups != null) {
            sourcesAvailableListener.secondLevelSourcesAvailable(requestCode,sourcesGroups);
            return;
        }

        if(!isGroupDownloadingStarted) {
            new GroupsDownloader().execute(requestCode);
        }
    }

    void getFriends(int requestCode) {
        if(sourcesFriends != null) {
            sourcesAvailableListener.secondLevelSourcesAvailable(requestCode,sourcesFriends);
            return;
        }

        if(!isFriendsDownloadingStarted) {
            new FriendsDownloader().execute(requestCode);
        }
    }

    void getSubscriptions(int requestCode) {
        if(sourcesSubscriptions != null) {
            sourcesAvailableListener.secondLevelSourcesAvailable(requestCode,sourcesSubscriptions);
            return;
        }

        if(!isSubscriptionsDownloadingStarted) {
            new SubscriptionsDownloader().execute(requestCode);
        }
    }

    public void setSourcesAvailableListener(SourcesAvailableListener sourcesAvailableListener) {
        this.sourcesAvailableListener = sourcesAvailableListener;
    }

    public void getAlbumsList(long vkSourceID) {
        //кодом запроса будет являться id источника новости
        if(vkSourceID == 0) {
            //это запрос для моих аудио
            if(lastRequestCodeForGetAlbumsWasZero) {
                if(lastAlbumsList != null) {
                    sourcesAvailableListener.thirdLevelSourcesAvailable(vkSourceID,lastAlbumsList);
                }
            } else {
                lastRequestCodeForGetAlbumsWasZero = true;
                lastAlbumsList = null;
                new AlbumsDownloader().execute(vkSourceID);
            }
            return;
        }

        lastRequestCodeForGetAlbumsWasZero = false;

        if(lastRequestCodeForGetAlbums == vkSourceID) {
            if(lastAlbumsList != null) {
                sourcesAvailableListener.thirdLevelSourcesAvailable(vkSourceID,lastAlbumsList);
            }

        } else {
            lastRequestCodeForGetAlbums = vkSourceID;
            lastAlbumsList = null;
            new AlbumsDownloader().execute(vkSourceID);
        }
    }

    public void getPopularAlbums(long requestCode) {
        sourcesAvailableListener.thirdLevelSourcesAvailable(requestCode,popularAlbums);
    }

    interface SourcesAvailableListener {
        /**
         * Доступны исочники аудио второго уровня (Друзья, Группы, Подписки)
         * @param requestCode код с которым был вызван метод загрузки источников
         * @param vkSources источники
         */
        void secondLevelSourcesAvailable(int requestCode, ArrayList<VkSource> vkSources);

        /**
         * Доступны альбомы конкретного пользователя
         * @param requestCode это код запроса (id пользователя)
         * @param albums альбомы + один стандартный альбом "Все аудио"
         */
        void thirdLevelSourcesAvailable(long requestCode, ArrayList<VKAudioAlbumItem> albums);
    }

    class GroupsDownloader extends AsyncTask<Integer,Void,ArrayList<VkSource>> {
        private int requestCode;
        private Gson gson = new Gson();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            isGroupDownloadingStarted = true;
        }

        /**
         * обратимся к хранимой процедуре getAllG
         передаваемые параметры отсутствуют
         процедура возвращает массив с ответами на запросы "groups.get"

         тело:
         {
         var r = [];
         var a = API.groups.get({"extended":1});
         r.push(a);
         var c = a.count;
         c = c - 1000;

         while(c>0) {
         r.push(API.groups.get({"extended":1}));
         c = c - 1000;
         }

         return r;
         }
         * @param integers
         * @return
         */
        @Override
        protected ArrayList<VkSource> doInBackground(Integer... integers) {
            requestCode = integers[0];

            VKRequest request = new VKRequest("execute.getAllG");
            request.attempts = 10;
            VKRequestManager requestManager = new VKRequestManager(REQUEST_TIMEOUT);
            requestManager.executeVKRequest(request);

            if (!requestManager.isSuccessfulCompleted()) {
                return null;
            }


            ResponseFromGroupsGetRequest[] responses =
                    gson.fromJson(Utils.getVKResponseString(requestManager.getResponse(), true),
                            ResponseFromGroupsGetRequest[].class);

            ArrayList<VkSource> sources = new ArrayList<>(responses[0].count);

            for (ResponseFromGroupsGetRequest resp : responses) {
                for (VkGroupItem groupItem : resp.items) {
                    groupItem.id = -groupItem.id;
                }
                Collections.addAll(sources, resp.items);
            }

            return sources;
        }

        @Override
        protected void onPostExecute(ArrayList<VkSource> vkSources) {
            super.onPostExecute(vkSources);
            if(vkSources != null) {
                sourcesGroups = vkSources;
            } else {
                isGroupDownloadingStarted = false;
            }

            if(sourcesAvailableListener != null) {
                sourcesAvailableListener.secondLevelSourcesAvailable(requestCode,vkSources);
            }
        }
    }

    class FriendsDownloader extends AsyncTask<Integer,Void,ArrayList<VkSource>> {
        private int requestCode;
        private Gson gson = new Gson();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            isFriendsDownloadingStarted = true;
        }

        /**
         * обратимся к хранимой процедуре getAllF
         передаваемые параметры отсутствуют
         процедура возвращает массив с ответами на запросы "friends.get"

         тело:
         {
         var r = [];
         var a = API.friends.get({"order":"hints","fields":"photo_50, photo_100"});
         r.push(a);

         if(a.count > 5000) {
         r.push(API.friends.get({"order":"hints","fields":"photo_50, photo_100","offset":5000}));
         }

         return r;
         }
         * @param integers
         * @return
         */
        @Override
        protected ArrayList<VkSource> doInBackground(Integer... integers) {
            requestCode = integers[0];

            VKRequest request = new VKRequest("execute.getAllF");
            request.attempts = 10;
            VKRequestManager requestManager = new VKRequestManager(REQUEST_TIMEOUT);
            requestManager.executeVKRequest(request);

            if (!requestManager.isSuccessfulCompleted()) {
                return null;
            }

            ResponseFromFriendsGetRequest[] responses =
                    gson.fromJson(Utils.getVKResponseString(requestManager.getResponse(), true),
                            ResponseFromFriendsGetRequest[].class);

            ArrayList<VkSource> sources = new ArrayList<>(responses[0].count);

            for (ResponseFromFriendsGetRequest resp : responses) {
                Collections.addAll(sources, resp.items);
            }


            return sources;
        }

        @Override
        protected void onPostExecute(ArrayList<VkSource> vkSources) {
            super.onPostExecute(vkSources);
            if(vkSources != null) {
                sourcesFriends = vkSources;
            } else {
                isFriendsDownloadingStarted = false;
            }

            if(sourcesAvailableListener != null) {
                sourcesAvailableListener.secondLevelSourcesAvailable(requestCode,vkSources);
            }
        }
    }

    class SubscriptionsDownloader extends AsyncTask<Integer,Void,ArrayList<VkSource>> {
        private int requestCode;
        private Gson gson = new Gson();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            isSubscriptionsDownloadingStarted = true;
        }

        @Override
        protected ArrayList<VkSource> doInBackground(Integer... integers) {
            requestCode = integers[0];
            //получим ссылки на людей, на которых у пользователя оформлена подписка
            VKRequest request = new VKRequest("users.getSubscriptions");
            request.attempts = 10;
            VKRequestManager requestManager = new VKRequestManager(REQUEST_TIMEOUT);
            requestManager.executeVKRequest(request);

            if (!requestManager.isSuccessfulCompleted()) {
                return null;
            }

            ResponseFromSubscriptionsGetRequest response1 =
                    gson.fromJson(Utils.getVKResponseString(requestManager.getResponse(), false),
                    ResponseFromSubscriptionsGetRequest.class);

            long[] subscribersIDs =  response1.users.items;
            if(subscribersIDs.length == 0) return new ArrayList<>();

            StringBuilder sb = new StringBuilder(String.valueOf(subscribersIDs[0]));

            for (long id : subscribersIDs)
                sb.append(",").append(String.valueOf(id));

            //получим объекты пользователей
            request = new VKRequest("users.get", VKParameters.from("user_ids", sb.toString(),
                    "fields", "photo_100"));
            request.attempts = 10;
            requestManager.executeVKRequest(request);

            if (!requestManager.isSuccessfulCompleted()) {
                return null;
            }

            VkUserItem[] userItems =
                    gson.fromJson(Utils.getVKResponseString(requestManager.getResponse(), true),
                            VkUserItem[].class);

            ArrayList<VkSource> sources = new ArrayList<>(userItems.length);
            Collections.addAll(sources, userItems);

            return sources;
        }

        @Override
        protected void onPostExecute(ArrayList<VkSource> vkSources) {
            super.onPostExecute(vkSources);
            if(vkSources != null) {
                sourcesSubscriptions = vkSources;
            } else {
                isSubscriptionsDownloadingStarted = false;
            }

            if(sourcesAvailableListener != null) {
                sourcesAvailableListener.secondLevelSourcesAvailable(requestCode,vkSources);
            }
        }
    }

    class AlbumsDownloader extends AsyncTask<Long,Void,ArrayList<VKAudioAlbumItem>> {
        private long vkSourceID;
        private Gson gson = new Gson();

        @Override
        protected ArrayList<VKAudioAlbumItem> doInBackground(Long... longs) {
            vkSourceID = longs[0];
            //получим список альбомов для заданного id владельца
            String firstAlbumName, secondAlbumName;
            VKRequest request;
            if(vkSourceID != 0) {
                request = new VKRequest("audio.getAlbums", VKParameters.from("count", 100, "owner_id", vkSourceID));
                firstAlbumName = VkOfflinePlayerApplication.getAppResources().getString(R.string.Default_audio_album_tittle) +
                        " "+vkAudioAlbumsTreeFragment.getCurrentVkSourceOfSecondLevelTitle();
                secondAlbumName = VkOfflinePlayerApplication.getAppResources().getString(R.string.Wall) + " "
                        + vkAudioAlbumsTreeFragment.getCurrentVkSourceOfSecondLevelTitle();
            } else {
                request = new VKRequest("audio.getAlbums", VKParameters.from("count", 100));
                firstAlbumName = VkOfflinePlayerApplication.getAppResources().getString(R.string.my)
                        + VkOfflinePlayerApplication.getAppResources().getString(R.string.Default_audio_album_tittle);
                secondAlbumName = VkOfflinePlayerApplication.getAppResources().getString(R.string.my2)
                        + VkOfflinePlayerApplication.getAppResources().getString(R.string.Wall);
            }
            request.attempts = 10;
            VKRequestManager requestManager = new VKRequestManager(REQUEST_TIMEOUT);
            requestManager.executeVKRequest(request);

            if (!requestManager.isSuccessfulCompleted()) {
                if(Utils.createResult(requestManager,null).getResStatus() == Result.STATUS_ACCESS_DENIED_ERROR) {
                    ArrayList<VKAudioAlbumItem> albumItems = new ArrayList<>(1);
                    albumItems.add(VKAudioAlbumItem.getWallAudioAlbum(vkSourceID, secondAlbumName));
                    return albumItems;
                } else return null;
            }

            ResponseFromAudioGetAlbumsRequest response1 =
                    gson.fromJson(Utils.getVKResponseString(requestManager.getResponse(), false),
                            ResponseFromAudioGetAlbumsRequest.class);

            int n = response1.items.length + 2;
            ArrayList<VKAudioAlbumItem> albumItems = new ArrayList<>(n);

            albumItems.add(VKAudioAlbumItem.getDefaultAudioAlbum(vkSourceID, firstAlbumName));
            albumItems.add(VKAudioAlbumItem.getWallAudioAlbum(vkSourceID, secondAlbumName));

            if(response1.count > 0) {
                Collections.addAll(albumItems, response1.items);
            }

            return albumItems;
        }

        @Override
        protected void onPostExecute(ArrayList<VKAudioAlbumItem> albums) {
            super.onPostExecute(albums);
            //здесь при неуспешном выполнении запроса присваивается код 0, что соответствует
            //успешному выполнению запроса на получение списка своих альбомов поэтому необходимо
            //указать что последний запрос на получение был не ноль
            //в этом случае при повторной попытке получить список альбомов все отработает корректно
            if(albums == null) {
                lastRequestCodeForGetAlbumsWasZero = false;
                lastRequestCodeForGetAlbums = 0;
            }
            lastAlbumsList = albums;
            if(sourcesAvailableListener != null) {
                sourcesAvailableListener.thirdLevelSourcesAvailable(vkSourceID, albums);
            }
        }
    }
}
