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
    [self update:YES :YES :NO];
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    return 6;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    switch (indexPath.row) {
        case 0:
            return [tableView dequeueReusableCellWithIdentifier:@"logoCell" forIndexPath:indexPath];
        case 1:
        case 4:
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
                    self.timer = (UILabel *)[[[cell contentView] subviews] objectAtIndex:i];
                    
                    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
                        
                        while (true) {
                            
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
                                    [prefs removeObjectForKey:@"user"];
                                    [self performSegueWithIdentifier:@"authSegue" sender:self];
                                    return;
                                }
                                NSLog(@"%@",response);
                                
                                if ([[response objectForKey:@"state"] isEqualToString:@"PLAY"]) {
                                    double timestamp = [[response objectForKey:@"current_time"] doubleValue];
                                    double started = [[[response objectForKey:@"current"] objectForKey:@"started"] doubleValue];
                                    
                                    
                                    double elapsed = (timestamp - started) / 1000;
                                    double trackLength = [[[response objectForKey:@"current"] objectForKey:@"length"] doubleValue] / 60;
                                    
                                    dispatch_async(dispatch_get_main_queue(), ^{
                                        self.timer.text = [NSString stringWithFormat:@"%f of %f", elapsed, trackLength];
                                        if (self.titleLabel) {
                                            self.titleLabel.text = [[response objectForKey:@"current"] objectForKey:@"title"];
                                        }
                                    });
                                } else {
                                    double elapsed = [[response objectForKey:@"position"] doubleValue];
                                    double trackLength = [[[response objectForKey:@"current"] objectForKey:@"length"] doubleValue] / 60;
                                    
                                    dispatch_async(dispatch_get_main_queue(), ^{
                                        self.timer.text = [NSString stringWithFormat:@"%f of %f", elapsed, trackLength];
                                        self.titleLabel.text = [[response objectForKey:@"current"] objectForKey:@"title"];
                                    });
                                }
                                
                            }
                            
                            
                            [NSThread sleepForTimeInterval:1.0f];
                        }
                        
                        
                    });
                    
                    

                    break;
                }
            }
            return cell;
        }
        case 5: {
            
            
            UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:@"controls" forIndexPath:indexPath];
            
            for (int i = 0; i < [[[cell contentView] subviews] count]; i++) {
                id thisItem = [[[cell contentView] subviews] objectAtIndex:i];
                if ([thisItem isKindOfClass:[UIButton class]]) {
                    UIButton *btn = (UIButton *)thisItem;
                    /*switch (btn.tag) {
                        case 1:
                            [btn addSubview:[self getControlImage:@"previous-active" :0]];
                    }*/
                }
            }
            //[cell addSubview:[self getControlImage:@"previous-active" :40]];
            //[cell addSubview:[self getControlImage:@"play-active" :100]];

            //[cell addSubview:[self getControlImage:@"pause-active" :160]];
            //[cell addSubview:[self getControlImage:@"next-active" :220]];

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
    return indexPath.row == 0 ? 80 : 60;
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
