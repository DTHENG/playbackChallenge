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
@property UILabel *timer;
@property UITableViewCell *device_playing;
@property BOOL isActive;

@end
