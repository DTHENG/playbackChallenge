//
//  PlayViewController.m
//  Spela
//
//  Created by Daniel Thengvall on 8/21/14.
//  Copyright (c) 2014 Daniel Thengvall. All rights reserved.
//

#import "PlayViewController.h"

@implementation PlayViewController

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

- (void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];
    
    self.isActive = YES;
    
    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
        
        while (self.isActive) {
            
            //NSLog(@"api request");
            NSData *urlData;
            NSURLResponse *response;
            NSError *error;
            NSUserDefaults *prefs = [NSUserDefaults standardUserDefaults];
            NSString *url = [NSString stringWithFormat:@"http://playback.dtheng.com/api?user=%@%@", [prefs objectForKey:@"firstName"], [prefs objectForKey:@"lastInitial"]];
            urlData = [NSURLConnection sendSynchronousRequest:[[NSURLRequest alloc] initWithURL:
                                                               [[NSURL alloc] initWithString:
                                                                url]]
                                            returningResponse:&response
                                                        error:&error];
            if (urlData != nil) {
                NSError *jsonParsingError = nil;
                NSDictionary *response = [NSJSONSerialization JSONObjectWithData:urlData options:0 error:&jsonParsingError];
                
                if (response == nil) {
                    [prefs removeObjectForKey:@"auth"];
                    [prefs removeObjectForKey:@"firstName"];
                    [prefs removeObjectForKey:@"lastInitial"];
                    [prefs synchronize];
                    [self performSegueWithIdentifier:@"authSegue" sender:self];
                    return;
                }
                //NSLog(@"%@",response);
                
                for (int i = 0; i < [[response objectForKey:@"devices"] count]; i++) {
                    if ([[[[response objectForKey:@"devices"] objectAtIndex:i] objectForKey:@"is_playing"] boolValue]) {
                        dispatch_async(dispatch_get_main_queue(), ^{
                            if (self.device_playing != nil) {
                                self.device_playing.textLabel.text = [[[response objectForKey:@"devices"] objectAtIndex:i] objectForKey:@"name"];
                            }
                        });
                        break;
                    }
                }
                
                dispatch_async(dispatch_get_main_queue(), ^{

                    if ([response objectForKey:@"next"] == nil) {
                        self.next.enabled = NO;
                    } else {
                        self.next.enabled = YES;
                    }
                    
                    if ([response objectForKey:@"previous"] == nil) {
                        self.previous.enabled = NO;
                    } else {
                        self.previous.enabled = YES;
                    }
                });
                
                if ([[response objectForKey:@"state"] isEqualToString:@"PLAY"]) {
                    
                    double timestamp = [[response objectForKey:@"current_time"] doubleValue];
                    double started = [[[response objectForKey:@"current"] objectForKey:@"started"] doubleValue];
                    
                    
                    double elapsed = (timestamp - started) / 1000;
                    double trackLength = [[[response objectForKey:@"current"] objectForKey:@"length"] doubleValue];
                    
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
                    
                    
                    double elapsed = [[response objectForKey:@"position"] doubleValue];
                    double trackLength = [[[response objectForKey:@"current"] objectForKey:@"length"] doubleValue];
                    
                    dispatch_async(dispatch_get_main_queue(), ^{
                        self.playBtn.enabled = YES;
                        self.pauseBtn.enabled = NO;
                        
                        self.progress.progress = elapsed / trackLength;
                        self.artist.text = [[response objectForKey:@"current"] objectForKey:@"artist"];
                        self.titleLabel.text = [[response objectForKey:@"current"] objectForKey:@"title"];
                    });
                }
                
            }
            
            
            [NSThread sleepForTimeInterval:1.0f];
        }
        
        
    });
    

}

- (void)viewWillDisappear:(BOOL)animated {
    [super viewWillDisappear:animated];
    
    self.isActive = NO;
}


- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    switch (indexPath.row) {
        case 0:
            return [tableView dequeueReusableCellWithIdentifier:@"logoCell" forIndexPath:indexPath];
        case 1:
        case 5:
        case 7:
            return [tableView dequeueReusableCellWithIdentifier:@"blank" forIndexPath:indexPath];
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
            //[cell addSubview:[self getControlImage:@"previous-active" :40]];
            //[cell addSubview:[self getControlImage:@"play-active" :100]];

            //[cell addSubview:[self getControlImage:@"pause-active" :160]];
            //[cell addSubview:[self getControlImage:@"next-active" :220]];

            return cell;

        }
        case 8: {
            return [tableView dequeueReusableCellWithIdentifier:@"playingOnCell" forIndexPath:indexPath];
        }
        case 9: {
            UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:@"device_playing" forIndexPath:indexPath];
            self.device_playing = cell;
            return cell;

        }


    }
    UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:@"blank" forIndexPath:indexPath];
    return cell;
}

- (UIImageView *)getControlImage:(NSString *)filename :(CGFloat)left {
    NSURL *url = [NSURL fileURLWithPath:[NSBundle.mainBundle pathForResource:filename ofType:@"pdf"]];
    CGSize size = CGSizeMake(308, 308); // Output image size
    CGPDFDocumentRef document = CGPDFDocumentCreateWithURL((CFURLRef)url);
    UIGraphicsBeginImageContextWithOptions(size, false, 0);
    CGContextRef context = UIGraphicsGetCurrentContext();
    CGContextTranslateCTM(context, 0.0, size.height);
    CGContextScaleCTM(context, 1.0, -1.0);
    CGPDFPageRef page = CGPDFDocumentGetPage(document, 1);
    CGContextConcatCTM(context, CGPDFPageGetDrawingTransform(page, kCGPDFBleedBox, CGRectMake(0, 0, size.width, size.height), 0, true));
    CGContextDrawPDFPage(context, page);
    UIImage *image = UIGraphicsGetImageFromCurrentImageContext();
    
    UIImageView *imageview = [[UIImageView alloc] initWithImage: image];
    
    [imageview setFrame:CGRectMake(left, 0, 60, 60)];
    //[imageview setBackgroundColor:[UIColor greenColor]];

    return imageview;

}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath {
    switch (indexPath.row) {
        case 0:
            return 80;
        case 5:
            return 5;
        case 1:
        case 7:
            return 20;
        case 8:
            return 40;
        default:
            return 60;
    }
}

- (void)update:(BOOL)play :(BOOL)next :(BOOL)previous {
    NSUserDefaults *prefs = [NSUserDefaults standardUserDefaults];
    NSString *post = [NSString stringWithFormat:@"&update=true&state=%@&user=%@%@&next=%@&previous=%@", play ? @"PLAY" : @"PAUSE", [prefs objectForKey:@"firstName"], [prefs objectForKey:@"lastInitial"], next ? @"true" : @"false", previous ? @"true" : @"false"];
    NSData *postData = [post dataUsingEncoding:NSASCIIStringEncoding allowLossyConversion:YES];
    NSString *postLength = [NSString stringWithFormat:@"%lu",(unsigned long)[postData length]];
    NSMutableURLRequest *request = [[NSMutableURLRequest alloc] init];
    [request setURL:[NSURL URLWithString:[NSString stringWithFormat:@"http://playback.dtheng.com/api"]]];
    [request setHTTPMethod:@"POST"];
    [request setValue:postLength forHTTPHeaderField:@"Content-Length"];
    [request setValue:@"application/x-www-form-urlencoded" forHTTPHeaderField:@"Current-Type"];
    [request setHTTPBody:postData];
    NSURLConnection *conn = [[NSURLConnection alloc]initWithRequest:request delegate:self];
    if ( ! conn) {
        NSLog(@"error making request");
    }
}

@end
