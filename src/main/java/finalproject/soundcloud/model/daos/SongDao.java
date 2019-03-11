package finalproject.soundcloud.model.daos;
import finalproject.soundcloud.model.dtos.ResponseDto;
import finalproject.soundcloud.model.pojos.Song;
import finalproject.soundcloud.model.pojos.User;
import finalproject.soundcloud.model.repostitories.SongRepository;
import finalproject.soundcloud.util.AmazonClient;
import finalproject.soundcloud.util.exceptions.DoesNotExistException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Component
public class SongDao {
    public static final int NORMAL_USER_UPLOAD_LIMIT = 180 * 60;
    public static final int LIMITED_PRO_UPLOAD_TIME = 360 * 60;
    @Autowired
    JdbcTemplate jdbcTemplate;
    @Autowired
    SongRepository songRepository;
    @Autowired
    CommentDao commentDao;
    @Autowired
    PlaylistDao playlistDao;
    @Autowired
    AmazonClient amazonClient;
    public static final String SONGS_DIR = "D:\\ITtalents\\FinalProject\\";
    @Transactional
    public ResponseDto rateSong(long songId, User user, boolean clickedLike) throws DoesNotExistException {
        String sql;
         // check if the song has been liked/disliked before
         sql = "DELETE FROM users_disliked_songs WHERE song_id = ? AND user_id = ?";
         int changed = jdbcTemplate.update(sql,songId,user.getId());
        Song song = songRepository.findById(songId);
        if(changed != 0){
             song.setDislikes(song.getDislikes() - 1);
             songRepository.save(song);
             if(!clickedLike)
                return new ResponseDto("song removed from disliked!");
         }
         sql = " DELETE FROM users_liked_songs WHERE song_id = ? AND user_id = ?";
         changed = jdbcTemplate.update(sql,songId,user.getId());
         if(changed != 0 ){
             song.setLikes(song.getLikes() - 1);
             songRepository.save(song);
             if (clickedLike)
                return new ResponseDto("song has been removed from liked!");
         } // if it hasn`t been like/dislike it now
         if(clickedLike){
             sql = "INSERT INTO users_liked_songs(user_id,song_id) VALUES(?,?)";
             song.setLikes(song.getLikes() + 1);
         }
         else{
             sql = "INSERT INTO users_disliked_songs(user_id,song_id) VALUES(?,?)";
             song.setDislikes(song.getDislikes() + 1);
         }
         songRepository.save(song);
        jdbcTemplate.update(sql, user.getId(),songId);
        ResponseDto responseDto = new ResponseDto();
         responseDto.setResponse("action complete!");
         return responseDto;
    }

    public ResponseDto repostSong(User user, long songId){
        String sql = "INSERT INTO users_reposts(user_id,song_id) VALUES(?,?)";
        jdbcTemplate.update(sql,user.getId(),songId);
        return new ResponseDto("song was reposted to your profile");
    }

    public ResponseDto unpostSong(User user,long songId){
        String sql = "DELETE FROM users_reposts WHERE user_id = ? AND song_id = ?";
        jdbcTemplate.update(sql,user.getId(),songId);
        return new ResponseDto("song unposted");
    }

    public boolean hasItPosted(User user, long songId) {
        String sql = "SELECT * FROM users_reposts WHERE user_id = ? AND song_id = ?";
         List<Song> songs= jdbcTemplate.query(sql,new Object[]{user.getId(),songId}, new BeanPropertyRowMapper<>(Song.class));
         return !songs.isEmpty();
    }

    public boolean canUploadSong(User user, File song){
        if(user.getUserType() > 2)
            return true;
        long currentUploadTime = getUserCurrentUploadTime(user);
        long currentSongDuration = getSongDuration(song).getSeconds();
        long totalSongsDuration = currentSongDuration + currentUploadTime ;
        if(user.getUserType() == 1 && (totalSongsDuration > NORMAL_USER_UPLOAD_LIMIT)) {
            return false;
        }
        if(user.getUserType() == 2 && totalSongsDuration > LIMITED_PRO_UPLOAD_TIME){
            return false;
        }
        return true;
    }

    private  long getUserCurrentUploadTime(User user) {
        long totalTime = 0;
        ArrayList<Song> songs = songRepository.findAllByUserId(user.getId());
        for (Song s : songs){
            totalTime +=(long) s.getLength();
        }
        return totalTime;
    }

    public Duration getSongDuration(File file) {
        AudioInputStream audioInputStream = null;
        try {
            audioInputStream = AudioSystem.getAudioInputStream(file);
        } catch (UnsupportedAudioFileException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        AudioFileFormat fileFormat = null;
        try {
            fileFormat = AudioSystem.getAudioFileFormat(file);
        } catch (UnsupportedAudioFileException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        long frames = fileFormat.getFrameLength(); // I get the frame length from file format, not InputStream
        AudioFormat format = audioInputStream.getFormat();
        double durationInSeconds = (frames) / format.getFrameRate();
        return Duration.ofSeconds(Math.round(durationInSeconds));
    }

    public void uploadSong(String filePath,File song , long songId,String songName) {
        String sql = "UPDATE songs SET  file_path = ? , length = ? WHERE song_id = ? ";
        jdbcTemplate.update(sql,filePath,getSongDuration(song).getSeconds(),songId);
        System.out.println(song.getName());
        amazonClient.uploadFile(song,songName);

    }
    public boolean deleteSong(Song song)  {
       
        String sql = "DELETE FROM songs WHERE song_id = ?";
        int done = jdbcTemplate.update(sql,song.getId());
        //amazonClient.deleteFileFromS3Bucket(song.getSongName());

        return done != 0;
    }

    public boolean deleteAllUserSongs(long userId){
        ArrayList<Song> songs = songRepository.findAllByUserId(userId);
        for (Song s : songs){
            deleteSong(s);
        }
        return true;
    }
}
