//
//  DevicesViewController.m
//  Spela
//
//  Created by Daniel Thengvall on 8/23/14.
//  Copyright (c) 2014 Daniel Thengvall. All rights reserved.
//

#import "DevicesViewController.h"

@implementation DevicesViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    
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
        
        self.devices = [response objectForKey:@"devices"];
        
        
    }
    
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    return 1 + [self.devices count];
}



- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    switch (indexPath.row) {
        case 0:
            return [tableView dequeueReusableCellWithIdentifier:@"logoCell" forIndexPath:indexPath];
        default: {
            UITableViewCell *deviceCell = [tableView dequeueReusableCellWithIdentifier:@"deviceCell" forIndexPath:indexPath];
            deviceCell.textLabel.text = [[self.devices objectAtIndex:indexPath.row -1] objectForKey:@"name"];
            if ([[[self.devices objectAtIndex:indexPath.row -1] objectForKey:@"is_playing"] boolValue]) {
                deviceCell.accessoryType = UITableViewCellAccessoryCheckmark;
            }
            return deviceCell;
            //return [tableView dequeueReusableCellWithIdentifier:@"blank" forIndexPath:indexPath];
        }
        
    }
    UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:@"blank" forIndexPath:indexPath];
    return cell;
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath {
    switch (indexPath.row) {
        case 0:
            return 80;
        default:
            return 50;
    }
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
    if (indexPath.row > 0) {
        NSUserDefaults *prefs = [NSUserDefaults standardUserDefaults];
        NSString *post = [NSString stringWithFormat:@"&update=true&device_id=%@&user=%@%@", [[[self.devices objectAtIndex:indexPath.row -1] objectForKey:@"name"] stringByReplacingOccurrencesOfString:@" " withString:@"+"], [prefs objectForKey:@"firstName"], [prefs objectForKey:@"lastInitial"]];
        NSData *postData = [post dataUsingEncoding:NSASCIIStringEncoding allowLossyConversion:YES];
        NSString *postLength = [NSString stringWithFormat:@"%lu",(unsigned long)[postData length]];
        NSMutableURLRequest *request = [[NSMutableURLRequest alloc] init];
        [request setURL:[NSURL URLWithString:[NSString stringWithFormat:@"http://playback.dtheng.com/api"]]];
        [request setHTTPMethod:@"POST"];
        [request setValue:postLength forHTTPHeaderField:@"Content-Length"];
        [request setValue:@"application/x-www-form-urlencoded" forHTTPHeaderField:@"Current-Type"];
        [request setHTTPBody:postData];
        NSURLConnection *conn = [[NSURLConnection alloc]initWithRequest:request delegate:self];
        
        [self dismissViewControllerAnimated:YES completion:nil];
    }
}

@end
