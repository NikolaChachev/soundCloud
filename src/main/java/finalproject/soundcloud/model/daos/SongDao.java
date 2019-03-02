package finalproject.soundcloud.model.daos;

import finalproject.soundcloud.model.dtos.ResponseDto;
import finalproject.soundcloud.model.dtos.SongDto;
import finalproject.soundcloud.model.pojos.Song;
import finalproject.soundcloud.model.pojos.User;
import finalproject.soundcloud.model.repostitories.SongRepository;
import finalproject.soundcloud.util.exceptions.DoesNotExistException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

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
    public static final String SONGS_DIR = "D:\\ITtalents\\FinalProject\\";
    public ResponseDto rateSong(long songId, User user, boolean clickedLike) throws DoesNotExistException {
        String sql = "SELECT * FROM songs WHERE song_id LIKE ?";
         List<Song> songs =  jdbcTemplate.query(sql,new Object[]{songId},new BeanPropertyRowMapper<>(Song.class));
         if(songs.isEmpty()){ // check if the song exists
             throw new DoesNotExistException("song");
         }
         // check if the song has been liked/disliked before
         sql = "DELETE FROM users_disliked_songs WHERE song_id = ?";
         int changed = jdbcTemplate.update(sql,songId);
         if(changed != 0){
             Song song = songRepository.findById(songId);
             song.setDislikes(song.getDislikes() - 1);
             songRepository.save(song);
             if(!clickedLike)
                return new ResponseDto("song removed from disliked!");
         }
         sql = " DELETE FROM users_liked_songs WHERE song_id = ?";
         changed = jdbcTemplate.update(sql,songId);
         if(changed != 0 ){
             Song song = songRepository.findById(songId);
             song.setLikes(song.getLikes() - 1);
             songRepository.save(song);
             if (clickedLike)
                return new ResponseDto("song has been removed from liked!");
         } // if it hasn`t been like/dislike it now
         if(clickedLike){
             sql = "INSERT INTO users_liked_songs(user_id,song_id) VALUES(?,?)";
         }
         else{
             sql = "INSERT INTO users_disliked_songs(user_id,song_id) VALUES(?,?)";
         }
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
            File song = new File(SONGS_DIR + s.getFilePath());
            totalTime += getSongDuration(song).getSeconds();
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

    public void uploadSong(String name, User user, SongDto dto,File song) {
        String sql = "INSERT INTO songs(user_id,song_name,is_public,file_path,length) " +
                "VALUES(?,?,?,?,?);";
        jdbcTemplate.update(sql,user.getId(),dto.getSongName(),dto.isPublic(),name,getSongDuration(song).getSeconds());
    }
}
