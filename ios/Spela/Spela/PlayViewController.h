#import "BaseTableViewController.h"

// author : Daniel Thengvall

@interface PlayViewController : BaseTableViewController

// the name of the current song
@property UILabel *titleLabel;

// the name of the artist
@property UILabel *artist;

// the device playing button
@property UITableViewCell *device_playing;

// is this table view still being displayed?
@property BOOL isActive;

// the songs progress
@property UIProgressView *progress;

// go to previous track button
@property UIButton *previous;

// play track button
@property UIButton *playBtn;

// pause track button
@property UIButton *pauseBtn;

// go to next track button
@property UIButton *next;

@end
