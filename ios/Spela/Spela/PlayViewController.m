#import "PlayViewController.h"

// author : Daniel Thengvall

@implementation PlayViewController

// Executed when player is being displayed or refreshed
- (void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];
    
    // Set this objects internal state so as to prevent it from continuing in an infinite for loop if obj-c has removed and replaced with new
    self.isActive = YES;
    
    // Start infinate for loop in new thread
    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
        
        // Should this thread proceed?
        while (self.isActive) {
            NSData *urlData;
            NSURLResponse *response;
            NSError *error;
            NSUserDefaults *prefs = [NSUserDefaults standardUserDefaults];
            
            // Build request url
            NSString *url = [NSString stringWithFormat:@"http://playback.dtheng.com/api?user=%@%@", [prefs objectForKey:@"firstName"], [prefs objectForKey:@"lastInitial"]];
            urlData = [NSURLConnection sendSynchronousRequest:[[NSURLRequest alloc] initWithURL:[[NSURL alloc] initWithString: url]] returningResponse:&response error:&error];
            if (urlData != nil) {
                NSError *jsonParsingError = nil;
                NSDictionary *response = [NSJSONSerialization JSONObjectWithData:urlData options:0 error:&jsonParsingError];
                if (response == nil) {
                    
                    // Deauthenticate user
                    [prefs removeObjectForKey:@"auth"];
                    [prefs removeObjectForKey:@"firstName"];
                    [prefs removeObjectForKey:@"lastInitial"];
                    [prefs synchronize];
                    
                    // Display login form
                    [self performSegueWithIdentifier:@"authSegue" sender:self];
                    return;
                }
                
                // Update active device
                for (int i = 0; i < [[response objectForKey:@"devices"] count]; i++) {
                    if ([[[[response objectForKey:@"devices"] objectAtIndex:i] objectForKey:@"is_playing"] boolValue]) {
                        
                        // Switch to main thread to execute updates
                        dispatch_async(dispatch_get_main_queue(), ^{
                            if (self.device_playing != nil) {
                                self.device_playing.textLabel.text = [[[response objectForKey:@"devices"] objectAtIndex:i] objectForKey:@"name"];
                            }
                        });
                        break;
                    }
                }
                
                // Switch to main thread to update next and previous button states
                dispatch_async(dispatch_get_main_queue(), ^{
                    self.next.enabled = [response objectForKey:@"next"] != nil;
                    self.previous.enabled = [response objectForKey:@"previous"] != nil;
                });
                if ([[response objectForKey:@"state"] isEqualToString:@"PLAY"]) {
                    
                    // Calculate relevant spacial data
                    double timestamp = [[response objectForKey:@"current_time"] doubleValue];
                    double started = [[[response objectForKey:@"current"] objectForKey:@"started"] doubleValue];
                    double elapsed = (timestamp - started) / 1000;
                    double trackLength = [[[response objectForKey:@"current"] objectForKey:@"length"] doubleValue];
                    
                    // Switch bace to main thread to make updates
                    dispatch_async(dispatch_get_main_queue(), ^{
                        self.playBtn.enabled = NO;
                        self.pauseBtn.enabled = YES;
                        self.progress.progress = elapsed / trackLength;
                        self.artist.text = [[response objectForKey:@"current"] objectForKey:@"artist"];
                        if (self.titleLabel) {
                            self.titleLabel.text = [[response objectForKey:@"current"] objectForKey:@"title"];
                        }
                    });
                } else {
                    
                    // Calcualte historical spacial data
                    double elapsed = [[response objectForKey:@"position"] doubleValue];
                    double trackLength = [[[response objectForKey:@"current"] objectForKey:@"length"] doubleValue];
                    
                    // Switch back to main thread to make updates
                    dispatch_async(dispatch_get_main_queue(), ^{
                        self.playBtn.enabled = YES;
                        self.pauseBtn.enabled = NO;
                        self.progress.progress = elapsed / trackLength;
                        self.artist.text = [[response objectForKey:@"current"] objectForKey:@"artist"];
                        self.titleLabel.text = [[response objectForKey:@"current"] objectForKey:@"title"];
                    });
                }
            }
            
            // Pause for one second before refreshing
            [NSThread sleepForTimeInterval:1.0f];
        }
    });
}

- (void)viewWillDisappear:(BOOL)animated {
    [super viewWillDisappear:animated];
    
    // Insure infinite look will not continue on into infinity
    self.isActive = NO;
}


- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    switch (indexPath.row) {
            
        // Spela logo
        case 0:
            return [tableView dequeueReusableCellWithIdentifier:@"logoCell" forIndexPath:indexPath];
            
        // Blank spaces
        case 1:
        case 5:
        case 7:
            return [tableView dequeueReusableCellWithIdentifier:@"blank" forIndexPath:indexPath];
        
        // Name of song currently playing
        case 2: {
            UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:@"songTitleCell" forIndexPath:indexPath];
            for (int i = 0; i < [[[cell contentView] subviews] count]; i++) {
                if ([[[[cell contentView] subviews] objectAtIndex:i] isKindOfClass:[UILabel class]]) {
                    self.titleLabel = (UILabel *)[[[cell contentView] subviews] objectAtIndex:i];
                    break;
                }
            }
            return cell;
        }
            
        // Name of artist
        case 3: {
            UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:@"artistCell" forIndexPath:indexPath];
            for (int i = 0; i < [[[cell contentView] subviews] count]; i++) {
                if ([[[[cell contentView] subviews] objectAtIndex:i] isKindOfClass:[UILabel class]]) {
                    self.artist = (UILabel *)[[[cell contentView] subviews] objectAtIndex:i];
                    break;
                }
            }
            return cell;
        }
            
        // The position of the current playing or paused song
        case 4: {
            UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:@"progressCell" forIndexPath:indexPath];
            for (int i = 0; i < [[[cell contentView] subviews] count]; i++) {
                if ([[[[cell contentView] subviews] objectAtIndex:i] isKindOfClass:[UIProgressView class]]) {
                    self.progress = (UIProgressView *)[[[cell contentView] subviews] objectAtIndex:i];
                    break;
                }
            }
            return cell;

        }
            
        // The player controlls
        case 6: {
            UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:@"controls" forIndexPath:indexPath];
            
            for (int i = 0; i < [[[cell contentView] subviews] count]; i++) {
                id thisItem = [[[cell contentView] subviews] objectAtIndex:i];
                if ([thisItem isKindOfClass:[UIButton class]]) {
                    UIButton *btn = (UIButton *)thisItem;
                    switch (btn.tag) {
                        case 1:
                            self.previous = btn;
                            break;
                        case 2:
                            self.playBtn = btn;
                            break;
                        case 3:
                            self.pauseBtn = btn;
                            break;
                        case 4:
                            self.next = btn;
                    }
                }
            }
            return cell;
        }
            
        // The "playing on" label
        case 8: {
            return [tableView dequeueReusableCellWithIdentifier:@"playingOnCell" forIndexPath:indexPath];
        }
            
        // Current device label/button
        case 9: {
            UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:@"device_playing" forIndexPath:indexPath];
            self.device_playing = cell;
            return cell;
        }
    }
    
    // Default cell
    UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:@"blank" forIndexPath:indexPath];
    return cell;
}


- (IBAction)previous:(id)sender {
    [self update:YES :NO :YES];
}

- (IBAction)play:(id)sender {
    [self update:YES :NO :NO];
}

- (IBAction)pause:(id)sender {
    [self update:NO :NO :NO];
}

- (IBAction)next:(id)sender {
    ((UIButton *)sender).enabled = NO;
    [self update:YES :YES :NO];
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    return 10;
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath {
    switch (indexPath.row) {
            
        // Spela logo
        case 0:
            return 80;
        
        // Blank cell between progress bar and controls
        case 5:
            return 5;
        
        // Blank cells
        case 1:
        case 7:
            return 20;
            
        // Playing on label cell
        case 8:
            return 40;
            
        // Every other cell
        default:
            return 60;
    }
}

// Preforms update requests
- (void)update:(BOOL)play :(BOOL)next :(BOOL)previous {
    NSUserDefaults *prefs = [NSUserDefaults standardUserDefaults];
    
    // Build request params
    NSString *post = [NSString stringWithFormat:@"&update=true&state=%@&user=%@%@&next=%@&previous=%@", play ? @"PLAY" : @"PAUSE", [prefs objectForKey:@"firstName"], [prefs objectForKey:@"lastInitial"], next ? @"true" : @"false", previous ? @"true" : @"false"];
    NSData *postData = [post dataUsingEncoding:NSASCIIStringEncoding allowLossyConversion:YES];
    NSString *postLength = [NSString stringWithFormat:@"%lu",(unsigned long)[postData length]];
    NSMutableURLRequest *request = [[NSMutableURLRequest alloc] init];
    
    // Set api endpoint
    [request setURL:[NSURL URLWithString:[NSString stringWithFormat:@"http://playback.dtheng.com/api"]]];
    [request setHTTPMethod:@"POST"];
    [request setValue:postLength forHTTPHeaderField:@"Content-Length"];
    [request setValue:@"application/x-www-form-urlencoded" forHTTPHeaderField:@"Current-Type"];
    [request setHTTPBody:postData];
    
    // Execute update request
    NSURLConnection *conn = [[NSURLConnection alloc]initWithRequest:request delegate:self];
    if ( ! conn) {
        NSLog(@"error making request");
    }
}

@end