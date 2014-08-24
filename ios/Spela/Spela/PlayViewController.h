//
//  PlayViewController.h
//  Spela
//
//  Created by Daniel Thengvall on 8/21/14.
//  Copyright (c) 2014 Daniel Thengvall. All rights reserved.
//

#import "BaseTableViewController.h"

@interface PlayViewController : BaseTableViewController

@property UILabel *titleLabel;
@property UILabel *artist;
@property UITableViewCell *device_playing;
@property BOOL isActive;
@property UIProgressView *progress;
@property UIButton *previous;
@property UIButton *playBtn;
@property UIButton *pauseBtn;
@property UIButton *next;

@end
